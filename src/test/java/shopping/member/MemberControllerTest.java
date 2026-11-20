package shopping.member;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import shopping.auth.JwtTokenProvider;
import shopping.common.GlobalExceptionHandler;

class MemberControllerTest {

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        JwtTokenProvider provider =
                new JwtTokenProvider("test-secret-key-for-jwt-test-1234567890", 3_600_000);
        MemberService service = new MemberService(new MemberRepository(), provider);
        mockMvc = MockMvcBuilders.standaloneSetup(new MemberController(service))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    private ResultActions send(String path, String email, String password) throws Exception {
        String body = "{\"email\":\"" + email + "\",\"password\":\"" + password + "\"}";
        return mockMvc.perform(post(path).contentType(MediaType.APPLICATION_JSON).content(body));
    }

    @Test
    void 가입하면_201과_토큰을_반환한다() throws Exception {
        String response = send("/api/members/register", "hong@example.com", "1234")
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);

        assertThat(response).contains("\"token\"");
    }

    @Test
    void 중복_이메일로_가입하면_409이다() throws Exception {
        send("/api/members/register", "hong@example.com", "1234");

        send("/api/members/register", "hong@example.com", "1234")
                .andExpect(status().isConflict());
    }

    @Test
    void 이메일_형식이_잘못되면_400이다() throws Exception {
        send("/api/members/register", "not-an-email", "1234")
                .andExpect(status().isBadRequest());
    }

    @Test
    void 로그인하면_200과_토큰을_반환한다() throws Exception {
        send("/api/members/register", "hong@example.com", "1234");

        send("/api/members/login", "hong@example.com", "1234")
                .andExpect(status().isOk());
    }

    @Test
    void 비밀번호가_틀리면_401이다() throws Exception {
        send("/api/members/register", "hong@example.com", "1234");

        send("/api/members/login", "hong@example.com", "wrong")
                .andExpect(status().isUnauthorized());
    }
}