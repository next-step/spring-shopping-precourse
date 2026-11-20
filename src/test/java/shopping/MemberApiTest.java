package shopping;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.jayway.jsonpath.JsonPath;
import jakarta.persistence.EntityManager;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest(
        properties = {
            "spring.datasource.url=jdbc:h2:mem:member-api;DB_CLOSE_DELAY=-1",
            "spring.datasource.driver-class-name=org.h2.Driver",
            "spring.datasource.username=sa",
            "spring.datasource.password=",
            "spring.jpa.hibernate.ddl-auto=create-drop",
            "spring.flyway.enabled=false"
        })
@AutoConfigureMockMvc
@Transactional
class MemberApiTest {

    @Autowired private MockMvc mockMvc;

    @Autowired private MemberRepository members;

    @Autowired private MemberTokenRepository tokens;

    @Autowired private PasswordEncoder passwordEncoder;

    @Autowired private EntityManager entityManager;

    @Test
    void registerPersistsNormalizedMemberAndReturnsTokenWithoutPassword() throws Exception {
        String token = register("  Member@Example.COM  ", "password123!");
        entityManager.flush();
        entityManager.clear();
        Member member = members.findByEmail("member@example.com").orElseThrow();
        assertThat(passwordEncoder.matches("password123!", member.getPasswordHash())).isTrue();
        assertThat(member.getPasswordHash()).isNotEqualTo("password123!");
        assertThat(tokens.findById(TokenService.hashToken(token)).orElseThrow().getMemberId())
                .isEqualTo(member.getId());
    }

    @Test
    void loginReturnsNewTokenForExistingMember() throws Exception {
        String first = register("member@example.com", "password123!");
        String body = credentials("  MEMBER@EXAMPLE.COM  ", "password123!");
        String second = login(body);
        entityManager.flush();
        assertThat(second).isNotEqualTo(first);
        assertThat(tokens.existsById(TokenService.hashToken(second))).isTrue();
        assertThat(members.count()).isEqualTo(1);
        assertThat(tokens.count()).isEqualTo(2);
    }

    private String login(String body) throws Exception {
        String json =
                request("login", body)
                        .andExpect(status().isOk())
                        .andExpect(header().string("Cache-Control", "no-store"))
                        .andExpect(jsonPath("$.passwordHash").doesNotExist())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();
        return JsonPath.read(json, "$.token");
    }

    @Test
    void duplicateEmailReturnsConflictWithoutExtraMemberOrToken() throws Exception {
        register("member@example.com", "password123!");
        request("register", credentials(" MEMBER@EXAMPLE.COM ", "another-password"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("DUPLICATE_EMAIL"));
        assertThat(members.count()).isEqualTo(1);
        assertThat(tokens.count()).isEqualTo(1);
    }

    @ParameterizedTest
    @CsvSource({"missing@example.com, password123!", "member@example.com, wrong-password"})
    void loginFailuresReturnSameErrorWithoutIssuingToken(String email, String password)
            throws Exception {
        register("member@example.com", "password123!");
        request("login", credentials(email, password))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("INVALID_CREDENTIALS"))
                .andExpect(jsonPath("$.message").value("이메일 또는 비밀번호가 올바르지 않습니다."));
        assertThat(tokens.count()).isEqualTo(1);
    }

    @ParameterizedTest
    @MethodSource("invalidRequests")
    void rejectsInvalidRegisterAndLoginWithoutSaving(String body) throws Exception {
        request("register", body)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_REQUEST"))
                .andExpect(jsonPath("$.message").isNotEmpty());
        request("login", body)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_REQUEST"));
        assertThat(members.count()).isZero();
        assertThat(tokens.count()).isZero();
    }

    static Stream<String> invalidRequests() {
        return Stream.of(
                "",
                "{}",
                "{",
                "{\"email\":\"member@example.com\",\"password\":{}}",
                credentials("bad-email", "password123!"),
                credentials("   ", "password123!"),
                credentials("member@example.com", "short"),
                credentials("member@example.com", "        "),
                credentials("member@example.com", "a".repeat(65)),
                credentials("member@example.com", "가".repeat(25)));
    }

    @Test
    void preservesPasswordSpacesAcrossRegisterAndLogin() throws Exception {
        register("member@example.com", " password123! ");
        request("login", credentials("member@example.com", " password123! "))
                .andExpect(status().isOk());
        request("login", credentials("member@example.com", "password123!"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("INVALID_CREDENTIALS"));
    }

    private String register(String email, String password) throws Exception {
        String json =
                request("register", credentials(email, password))
                        .andExpect(status().isCreated())
                        .andExpect(header().string("Cache-Control", "no-store"))
                        .andExpect(jsonPath("$.token").isNotEmpty())
                        .andExpect(jsonPath("$.tokenType").value("Bearer"))
                        .andExpect(jsonPath("$.expiresIn").value(86400))
                        .andExpect(jsonPath("$.password").doesNotExist())
                        .andExpect(jsonPath("$.passwordHash").doesNotExist())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();
        return JsonPath.read(json, "$.token");
    }

    private ResultActions request(String endpoint, String body) throws Exception {
        return mockMvc.perform(
                post("/api/members/" + endpoint)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body));
    }

    private static String credentials(String email, String password) {
        return """
               {"email":"%s","password":"%s"}
               """
                .formatted(email, password);
    }
}
