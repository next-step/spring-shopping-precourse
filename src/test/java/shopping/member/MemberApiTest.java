package shopping.member;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class MemberApiTest {

    private static final String MEMBER = "{\"email\": \"test@test.com\", \"password\": \"1234\"}";
    private static final String WRONG = "{\"email\": \"test@test.com\", \"password\": \"9999\"}";
    private static final String BAD_EMAIL = "{\"email\": \"testtest.com\", \"password\": \"1234\"}";

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("가입하면 201과 토큰을 받는다")
    void registers() throws Exception {
        send("/api/members/register", MEMBER)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").isNotEmpty());
    }

    @Test
    @DisplayName("같은 이메일로 가입하면 409")
    void returns409WhenDuplicate() throws Exception {
        send("/api/members/register", MEMBER);
        send("/api/members/register", MEMBER)
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("이미 가입된 이메일입니다."));
    }

    @Test
    @DisplayName("이메일 형식이 틀리면 400")
    void returns400WhenBadEmail() throws Exception {
        send("/api/members/register", BAD_EMAIL)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("이메일 형식이 올바르지 않습니다."));
    }

    @Test
    @DisplayName("로그인하면 200과 토큰을 받는다")
    void logsIn() throws Exception {
        send("/api/members/register", MEMBER);
        send("/api/members/login", MEMBER)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty());
    }

    @Test
    @DisplayName("비밀번호가 틀리면 401")
    void returns401WhenWrongPassword() throws Exception {
        send("/api/members/register", MEMBER);
        send("/api/members/login", WRONG)
                .andExpect(status().isUnauthorized());
    }

    private ResultActions send(String url, String body) throws Exception {
        return mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body));
    }
}