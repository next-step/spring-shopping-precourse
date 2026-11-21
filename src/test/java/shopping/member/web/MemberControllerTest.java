package shopping.member.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import shopping.auth.application.AuthenticationService;
import shopping.auth.application.TokenGenerator;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("회원 컨트롤러")
class MemberControllerTest {

    private static final String BODY =
            "{\"email\":\" yeji@example.com \",\"password\":\"hello123\"}";

    @Autowired
    private MockMvc mvc;
    @Autowired
    private JdbcTemplate jdbc;
    @MockitoSpyBean
    private TokenGenerator generator;
    @Autowired
    private AuthenticationService authentication;

    @BeforeEach
    @AfterEach
    void clear() {
        jdbc.update("delete from wishes");
        jdbc.update("delete from access_tokens");
        jdbc.update("delete from members");
    }

    @DisplayName("회원가입하면 앞뒤 공백을 제거한 이메일을 저장하고 토큰을 반환한다")
    @Test
    void returnsIssuedTokenAndStoresNormalizedMember() throws Exception {
        register(BODY).andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").isNotEmpty());

        assertThat(jdbc.queryForObject("select email from members", String.class))
                .isEqualTo("yeji@example.com");
        assertThat(jdbc.queryForObject("select count(*) from access_tokens", Integer.class))
                .isEqualTo(1);
    }

    @DisplayName("중복 이메일로 회원가입하면 충돌 응답을 반환한다")
    @Test
    void duplicateEmailReturnsConflict() throws Exception {
        register(BODY).andExpect(status().isCreated());

        register(BODY).andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("EMAIL_ALREADY_EXISTS"));
    }

    @DisplayName("잘못된 회원가입 요청은 공통 입력 오류로 응답한다")
    @ParameterizedTest
    @ValueSource(strings = {"{", "{}", "{\"email\":\"bad\",\"password\":\"hello123\"}",
            "{\"email\":\"yeji@example.com\",\"password\":12345}",
            "{\"email\":\"yeji@example.com\",\"password\":\"hello123\",\"id\":1}"})
    void invalidInputReturnsCommonError(String body) throws Exception {
        register(body).andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_REQUEST"));
    }

    @DisplayName("가입 토큰 발급에 실패하면 회원 저장도 취소한다")
    @Test
    void issuingFailureRollsBackMemberStorage() throws Exception {
        doThrow(new IllegalStateException("issuance failed")).when(generator).generate();

        register(BODY).andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.code").value("INTERNAL_SERVER_ERROR"));

        assertThat(jdbc.queryForObject("select count(*) from members", Integer.class)).isZero();
        assertThat(jdbc.queryForObject("select count(*) from access_tokens", Integer.class))
                .isZero();
    }

    @DisplayName("로그인하면 새 토큰을 발급하고 기존 토큰도 유효하게 유지한다")
    @Test
    void loginIssuesNewTokenAndKeepsPreviousTokenValid() throws Exception {
        String original = token(request("register", "yeji@example.com", "hello123"));
        String renewed = token(request("login", " yeji@example.com ", "hello123")
                .andExpect(status().isOk()));

        assertThat(renewed).isNotEqualTo(original);
        assertThat(authentication.authenticate(renewed))
                .isEqualTo(authentication.authenticate(original));
    }

    @DisplayName("미가입 이메일과 잘못된 비밀번호는 동일한 인증 오류를 반환한다")
    @ParameterizedTest
    @ValueSource(strings = {"missing@example.com", "yeji@example.com"})
    void missingEmailAndWrongPasswordReturnSameError(String email) throws Exception {
        request("register", "yeji@example.com", "hello123");

        request("login", email, "wrong123").andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("INVALID_CREDENTIALS"));
    }

    @DisplayName("이메일과 비밀번호 형식 오류는 입력 오류로 응답한다")
    @ParameterizedTest
    @CsvSource({"bad,hello123", "yeji@example.com,abcd"})
    void malformedCredentialsAreInputError(String email, String password) throws Exception {
        register(BODY).andExpect(status().isCreated());

        request("login", email, password).andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_REQUEST"));
    }

    private ResultActions register(String body) throws Exception {
        return mvc.perform(post("/api/members/register").contentType("application/json")
                .content(body));
    }

    private ResultActions request(String action, String email, String password) throws Exception {
        String body = "{\"email\":\"%s\",\"password\":\"%s\"}".formatted(email, password);
        return mvc.perform(post("/api/members/" + action).contentType("application/json")
                .content(body));
    }

    private String token(ResultActions response) throws Exception {
        return JsonPath.read(response.andReturn().getResponse().getContentAsString(), "$.token");
    }
}
