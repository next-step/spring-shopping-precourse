package shopping.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class MemberControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void 회원가입을_한다() throws Exception {
        mockMvc.perform(registerRequest("register@email.com", "1234"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists());
    }

    @Test
    void 이메일_형식이_잘못되면_400을_반환한다() throws Exception {
        mockMvc.perform(registerRequest("invalid", "1234"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void 비밀번호가_4자리_미만이면_400을_반환한다() throws Exception {
        mockMvc.perform(registerRequest("short@email.com", "123"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void 로그인을_한다() throws Exception {
        mockMvc.perform(registerRequest("login@email.com", "1234"));

        mockMvc.perform(loginRequest("login@email.com", "1234"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists());
    }

    @Test
    void 비밀번호가_틀리면_400을_반환한다() throws Exception {
        mockMvc.perform(registerRequest("wrong@email.com", "1234"));

        mockMvc.perform(loginRequest("wrong@email.com", "9999"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists());
    }

    private org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder registerRequest(
            String email, String password) {
        return post("/api/members/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(memberJson(email, password));
    }

    private org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder loginRequest(
            String email, String password) {
        return post("/api/members/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(memberJson(email, password));
    }

    private String memberJson(String email, String password) {
        return "{\"email\": \"%s\", \"password\": \"%s\"}".formatted(email, password);
    }
}
