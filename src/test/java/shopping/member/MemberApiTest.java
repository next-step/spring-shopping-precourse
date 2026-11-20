package shopping.member;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import shopping.ApiTest;

class MemberApiTest extends ApiTest {

    private static final String EMAIL = "guk@example.com";
    private static final String PASSWORD = "password1";

    @Autowired
    private MemberRepository memberRepository;

    @Test
    void 회원_가입하면_201과_토큰을_반환한다() throws Exception {
        request("/api/members/register", memberJson(EMAIL, PASSWORD))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").isNotEmpty());
    }

    @Test
    void 비밀번호는_해시값으로_저장된다() throws Exception {
        register(EMAIL, PASSWORD);

        Member member = memberRepository.findByEmail(EMAIL).orElseThrow();
        assertThat(member.getPassword()).isNotEqualTo(PASSWORD);
    }

    @Test
    void 이메일_형식이_잘못되면_400() throws Exception {
        request("/api/members/register", memberJson("guk", PASSWORD))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("올바른 이메일 형식이 아닙니다."));
    }

    @Test
    void 비밀번호가_비어_있으면_400() throws Exception {
        request("/api/members/register", memberJson(EMAIL, " "))
                .andExpect(status().isBadRequest());
    }

    @Test
    void 이미_가입된_이메일이면_400() throws Exception {
        register(EMAIL, PASSWORD);

        request("/api/members/register", memberJson(EMAIL, "password2"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("이미 가입된 이메일입니다."));
    }

    @Test
    void 로그인하면_200과_새_토큰을_반환한다() throws Exception {
        String registeredToken = register(EMAIL, PASSWORD);

        request("/api/members/login", memberJson(EMAIL, PASSWORD))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.token").value(not(registeredToken)));
    }

    @Test
    void 비밀번호가_틀리면_401() throws Exception {
        register(EMAIL, PASSWORD);

        request("/api/members/login", memberJson(EMAIL, "wrong-password"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("이메일 또는 비밀번호가 올바르지 않습니다."));
    }

    @Test
    void 가입하지_않은_이메일이면_401() throws Exception {
        request("/api/members/login", memberJson("nobody@example.com", PASSWORD))
                .andExpect(status().isUnauthorized());
    }

    private ResultActions request(String url, String json) throws Exception {
        return mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json));
    }
}
