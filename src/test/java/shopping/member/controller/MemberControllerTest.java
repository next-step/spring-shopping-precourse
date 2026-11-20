package shopping.member.controller;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import shopping.auth.JwtTokenProvider;
import shopping.member.domain.Member;
import shopping.member.exception.DuplicateEmailException;
import shopping.member.exception.InvalidCredentialsException;
import shopping.member.service.MemberService;

@WebMvcTest(MemberController.class)
class MemberControllerTest {
    private static final String EMAIL = "a@a.com";
    private static final String PASSWORD = "1234";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MemberService memberService;
    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    @Test
    void 가입하면_201과_회원정보를_돌려주고_비밀번호는_없다() throws Exception {
        given(memberService.register(EMAIL, PASSWORD))
                .willReturn(new Member(1L, EMAIL, "hashed"));

        postJson("/api/members/register", body(EMAIL, PASSWORD))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value(EMAIL))
                .andExpect(jsonPath("$.password").doesNotExist());
    }

    @Test
    void 이미_가입된_이메일이면_409와_메시지를_돌려준다() throws Exception {
        given(memberService.register(EMAIL, PASSWORD))
                .willThrow(new DuplicateEmailException("이미 가입된 이메일입니다."));

        postJson("/api/members/register", body(EMAIL, PASSWORD))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("이미 가입된 이메일입니다."));
    }

    @Test
    void 이메일_형식이_틀리면_400이고_서비스는_호출되지_않는다() throws Exception {
        postJson("/api/members/register", body("abc", PASSWORD))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(memberService);
    }

    @Test
    void 비밀번호가_비어_있으면_400이다() throws Exception {
        postJson("/api/members/register", body(EMAIL, ""))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(memberService);
    }

    @Test
    void 로그인하면_200과_토큰을_돌려준다() throws Exception {
        given(memberService.login(EMAIL, PASSWORD)).willReturn("token-value");

        postJson("/api/members/login", body(EMAIL, PASSWORD))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("token-value"));
    }

    @Test
    void 로그인에_실패하면_401과_메시지를_돌려준다() throws Exception {
        given(memberService.login(EMAIL, PASSWORD))
                .willThrow(new InvalidCredentialsException("이메일 또는 비밀번호가 올바르지 않습니다."));

        postJson("/api/members/login", body(EMAIL, PASSWORD))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("이메일 또는 비밀번호가 올바르지 않습니다."));
    }

    private ResultActions postJson(String uri, String body) throws Exception {
        return mockMvc.perform(post(uri)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body));
    }

    private String body(String email, String password) {
        return "{\"email\": \"%s\", \"password\": \"%s\"}".formatted(email, password);
    }
}