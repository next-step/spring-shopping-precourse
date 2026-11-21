package shopping.member;

import static org.assertj.core.api.Assertions.assertThat;

import com.jayway.jsonpath.JsonPath;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.test.web.servlet.assertj.MvcTestResult;
import shopping.auth.AuthToken;
import shopping.auth.AuthTokenRepository;

@SpringBootTest
@AutoConfigureMockMvc
class MemberApiTest {

    @Autowired
    private MockMvcTester mockMvc;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthTokenRepository authTokenRepository;

    @BeforeEach
    void setUp() {
        authTokenRepository.deleteAll();
        memberRepository.deleteAll();
    }

    @Test
    @DisplayName("이메일과 비밀번호를 입력받아 회원을 등록한다")
    void registerMember() {
        String request = """
                {"email": "test@example.com", "password": "password123"}
                """;

        assertThat(postRegister(request)).hasStatus(HttpStatus.CREATED);
        assertThat(memberRepository.findByEmail("test@example.com")).isPresent();
    }

    @Test
    @DisplayName("비밀번호를 해시 처리하여 저장한다")
    void savePasswordAsHash() {
        String request = """
                {"email": "hash@example.com", "password": "password123"}
                """;

        assertThat(postRegister(request)).hasStatus(HttpStatus.CREATED);
        String savedPassword = memberRepository.findByEmail("hash@example.com")
                .orElseThrow()
                .getPassword();
        assertThat(savedPassword).isNotEqualTo("password123");
        assertThat(passwordEncoder.matches("password123", savedPassword)).isTrue();
    }

    @Test
    @DisplayName("이미 가입된 이메일이면 회원가입을 거부한다")
    void rejectDuplicateEmail() {
        String request = """
                {"email": "dup@example.com", "password": "password123"}
                """;
        postRegister(request).exchange();

        assertThat(postRegister(request))
                .hasStatus(HttpStatus.CONFLICT)
                .bodyJson()
                .extractingPath("$.message").isEqualTo("이미 가입된 이메일입니다.");
        assertThat(memberRepository.count()).isEqualTo(1);
    }

    @Test
    @DisplayName("회원가입에 성공하면 UUID 인증 토큰을 발급한다")
    void issueTokenOnRegister() throws Exception {
        String request = """
                {"email": "token@example.com", "password": "password123"}
                """;

        MvcTestResult result = postRegister(request).exchange();

        assertThat(result).hasStatus(HttpStatus.CREATED);
        assertTokenIssued(result, "token@example.com");
    }

    @Test
    @DisplayName("이메일과 비밀번호가 일치하면 인증 토큰을 발급한다")
    void loginIssuesToken() throws Exception {
        String request = """
                {"email": "login@example.com", "password": "password123"}
                """;
        postRegister(request).exchange();

        MvcTestResult result = postLogin(request).exchange();

        assertThat(result).hasStatus(HttpStatus.OK);
        assertTokenIssued(result, "login@example.com");
    }

    @Test
    @DisplayName("비밀번호가 일치하지 않으면 로그인을 거부한다")
    void rejectWrongPassword() {
        String register = """
                {"email": "login@example.com", "password": "password123"}
                """;
        String login = """
                {"email": "login@example.com", "password": "wrong-password"}
                """;
        postRegister(register).exchange();

        assertThat(postLogin(login))
                .hasStatus(HttpStatus.UNAUTHORIZED)
                .bodyJson()
                .extractingPath("$.message").isEqualTo("이메일 또는 비밀번호가 일치하지 않습니다.");
    }

    @Test
    @DisplayName("가입되지 않은 이메일이면 로그인을 거부한다")
    void rejectUnknownEmail() {
        String request = """
                {"email": "nobody@example.com", "password": "password123"}
                """;

        assertThat(postLogin(request))
                .hasStatus(HttpStatus.UNAUTHORIZED)
                .bodyJson()
                .extractingPath("$.message").isEqualTo("이메일 또는 비밀번호가 일치하지 않습니다.");
    }

    private void assertTokenIssued(MvcTestResult result, String email) throws Exception {
        String token = JsonPath.read(result.getResponse().getContentAsString(), "$.token");
        assertThat(UUID.fromString(token)).hasToString(token);
        Member member = memberRepository.findByEmail(email).orElseThrow();
        assertThat(authTokenRepository.findByToken(token))
                .get()
                .extracting(AuthToken::getMemberId)
                .isEqualTo(member.getId());
    }

    private MockMvcTester.MockMvcRequestBuilder postRegister(String body) {
        return mockMvc.post()
                .uri("/api/members/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body);
    }

    private MockMvcTester.MockMvcRequestBuilder postLogin(String body) {
        return mockMvc.post()
                .uri("/api/members/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body);
    }
}
