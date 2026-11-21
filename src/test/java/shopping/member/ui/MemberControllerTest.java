package shopping.member.ui;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.test.web.servlet.assertj.MvcTestResult;
import org.springframework.transaction.annotation.Transactional;
import shopping.member.domain.MemberRepository;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class MemberControllerTest {

    @Autowired
    private MockMvcTester mockMvc;

    @Autowired
    private MemberRepository memberRepository;

    @Test
    @DisplayName("POST /api/members/register 로 회원 가입하면 201과 토큰을 응답하고 회원이 저장된다")
    void register() {
        String body = """
                {"email": "user@test.com", "password": "password"}
                """;

        MvcTestResult result = mockMvc.post().uri("/api/members/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
                .exchange();

        assertThat(result).hasStatus(HttpStatus.CREATED);
        assertThat(result).bodyJson().extractingPath("$.token").asString().isNotBlank();
        assertThat(memberRepository.count()).isEqualTo(1);
    }

    @Test
    @DisplayName("이메일 형식이 올바르지 않으면 400을 응답하고 회원이 저장되지 않는다")
    void registerWithInvalidEmail() {
        String body = """
                {"email": "not-email", "password": "password"}
                """;

        MvcTestResult result = mockMvc.post().uri("/api/members/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
                .exchange();

        assertThat(result).hasStatus(HttpStatus.BAD_REQUEST);
        assertThat(result).bodyJson().extractingPath("$.message").asString().isNotBlank();
        assertThat(memberRepository.count()).isZero();
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "{\"email\": \"\", \"password\": \"password\"}",
            "{\"password\": \"password\"}",
            "{\"email\": \"user@test.com\", \"password\": \"\"}",
            "{\"email\": \"user@test.com\"}"
    })
    @DisplayName("이메일 또는 비밀번호가 비어 있으면 400을 응답하고 회원이 저장되지 않는다")
    void registerWithBlankField(String body) {
        MvcTestResult result = mockMvc.post().uri("/api/members/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
                .exchange();

        assertThat(result).hasStatus(HttpStatus.BAD_REQUEST);
        assertThat(result).bodyJson().extractingPath("$.message").asString().isNotBlank();
        assertThat(memberRepository.count()).isZero();
    }

    @Test
    @DisplayName("POST /api/members/login 으로 가입한 이메일과 비밀번호를 보내면 200과 토큰을 응답한다")
    void login() {
        String body = """
                {"email": "user@test.com", "password": "password"}
                """;
        mockMvc.post().uri("/api/members/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
                .exchange();

        MvcTestResult result = mockMvc.post().uri("/api/members/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
                .exchange();

        assertThat(result).hasStatus(HttpStatus.OK);
        assertThat(result).bodyJson().extractingPath("$.token").asString().isNotBlank();
    }

    @Test
    @DisplayName("중복된 이메일로 회원 가입하면 409와 JSON 오류 메시지를 반환한다")
    void registerWithDuplicateEmail() {
        String body = """
                {"email": "user@test.com", "password": "password"}
                """;
        mockMvc.post().uri("/api/members/register")
                .contentType(MediaType.APPLICATION_JSON).content(body).exchange();

        MvcTestResult result = mockMvc.post().uri("/api/members/register")
                .contentType(MediaType.APPLICATION_JSON).content(body).exchange();

        assertThat(result).hasStatus(HttpStatus.CONFLICT);
        assertThat(result).bodyJson().extractingPath("$.message").asString().isNotBlank();
    }

    @Test
    @DisplayName("잘못된 로그인 정보를 보내면 401과 JSON 오류 메시지를 반환한다")
    void loginFailed() {
        String body = """
                {"email": "unknown@test.com", "password": "password"}
                """;

        MvcTestResult result = mockMvc.post().uri("/api/members/login")
                .contentType(MediaType.APPLICATION_JSON).content(body).exchange();

        assertThat(result).hasStatus(HttpStatus.UNAUTHORIZED);
        assertThat(result).bodyJson().extractingPath("$.message").asString().isNotBlank();
    }
}
