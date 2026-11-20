package shopping.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import shopping.dto.MemberRequest;
import shopping.exception.DuplicateEmailException;
import shopping.exception.InvalidCredentialsException;
import shopping.service.JwtProvider;
import shopping.service.AuthToken;
import shopping.service.MemberService;
import tools.jackson.databind.ObjectMapper;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MemberController.class)
class MemberControllerTests {

    private static final MemberRequest REQUEST = new MemberRequest("user@example.com", "password123");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private MemberService memberService;

    @MockitoBean
    private JwtProvider jwtProvider;

    @DisplayName("회원 가입하면 201과 함께 토큰을 응답한다")
    @Test
    void register() throws Exception {
        given(memberService.register("user@example.com", "password123")).willReturn(new AuthToken("token", 3600L));

        mockMvc.perform(post("/api/members/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(REQUEST)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.accessToken").value("token"))
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.expiresIn").value(3600));

        verify(memberService).register("user@example.com", "password123");
        verifyNoMoreInteractions(memberService);
    }

    @DisplayName("로그인하면 200과 함께 토큰을 응답한다")
    @Test
    void login() throws Exception {
        given(memberService.login("user@example.com", "password123")).willReturn(new AuthToken("token", 3600L));

        mockMvc.perform(post("/api/members/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(REQUEST)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("token"))
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.expiresIn").value(3600));
    }

    @DisplayName("로그인 정보가 일치하지 않으면 401로 응답한다")
    @Test
    void loginWithInvalidCredentials() throws Exception {
        given(memberService.login("user@example.com", "password123")).willThrow(new InvalidCredentialsException());

        mockMvc.perform(post("/api/members/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(REQUEST)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.detail").value("Invalid email or password"));
    }

    @DisplayName("이메일이나 비밀번호가 비어 있으면 400을 응답하고 서비스를 호출하지 않는다")
    @ParameterizedTest
    @ValueSource(strings = {"/api/members/register", "/api/members/login"})
    void blankRequest(String url) throws Exception {
        MemberRequest blank = new MemberRequest(" ", null);

        mockMvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(blank)))
                .andExpect(status().isBadRequest());
        verifyNoInteractions(memberService);
    }

    @DisplayName("이미 가입된 이메일이면 409로 응답한다")
    @Test
    void registerWithDuplicateEmail() throws Exception {
        given(memberService.register("user@example.com", "password123")).willThrow(new DuplicateEmailException());

        mockMvc.perform(post("/api/members/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(REQUEST)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.detail").value("Email already exists"));
    }

    @DisplayName("가입 시 도메인 규칙을 어기면 400으로 응답한다")
    @Test
    void registerWithInvalidValue() throws Exception {
        given(memberService.register("user@example.com", "password123"))
                .willThrow(new IllegalArgumentException("Password must be at least 8 characters"));

        mockMvc.perform(post("/api/members/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(REQUEST)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("Password must be at least 8 characters"));
    }
}
