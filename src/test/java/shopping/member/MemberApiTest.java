package shopping.member;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import shopping.AcceptanceTest;

class MemberApiTest extends AcceptanceTest {
    @Test
    void 회원_가입하면_토큰을_받는다() throws Exception {
        mockMvc.perform(post("/api/members/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(memberJson("user@example.com", "password123")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").exists());
    }

    @Test
    void 이미_가입된_이메일이면_409를_반환한다() throws Exception {
        register("user@example.com");

        mockMvc.perform(post("/api/members/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(memberJson("user@example.com", "password123")))
                .andExpect(status().isConflict());
    }

    @Test
    void 비밀번호가_8자_미만이면_400을_반환한다() throws Exception {
        mockMvc.perform(post("/api/members/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(memberJson("user@example.com", "short")))
                .andExpect(status().isBadRequest());
    }

    @Test
    void 로그인하면_토큰을_받는다() throws Exception {
        register("user@example.com");

        mockMvc.perform(post("/api/members/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(memberJson("user@example.com", "password123")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists());
    }

    @Test
    void 비밀번호가_틀리면_401을_반환한다() throws Exception {
        register("user@example.com");

        mockMvc.perform(post("/api/members/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(memberJson("user@example.com", "wrongpassword")))
                .andExpect(status().isUnauthorized());
    }
}
