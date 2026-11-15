package shopping.member.controller;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;
import shopping.auth.JwtProvider;
import shopping.common.exception.ErrorCode;
import shopping.member.repository.MemberRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class MemberControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired private MemberRepository memberRepository;
    @Autowired private JwtProvider jwtProvider;

    @Test
    void 회원가입에_성공하면_201과_토큰을_반환한다() throws  Exception {
        mockMvc.perform(post("/api/members/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"email": "a@a.com", "password": "qwer1234!"}
                        """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").isNotEmpty());
    }

//    @Test
//    void 회원가입_이메일이_형식에_맞지_않으면_400를_반환한다() throws Exception{
//    }

    @Test
    void 회원가입_이메일이_중복이면_409을_반환한다() throws Exception{
        mockMvc.perform(post("/api/members/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"email": "a@a.com", "password": "qwer1234!"}
                        """));

        mockMvc.perform(post("/api/members/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email": "a@a.com", "password": "other5678!"}
                                """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value(ErrorCode.DUPLICATE_EMAIL.name()));
    }

    @Test
    void 로그인_성공하면_200과_토큰을_반환한다() throws Exception{
        mockMvc.perform(post("/api/members/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {"email": "a@a.com", "password": "qwer1234!"}
                            """));

        MvcResult result = mockMvc.perform(post("/api/members/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {"email": "a@a.com", "password": "qwer1234!"}
                            """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andReturn();
        String body = result.getResponse().getContentAsString();
        String token = JsonPath.read(body, "$.token");
        Long memberId = memberRepository.findByEmail("a@a.com").orElseThrow().getId();

        assertThat(jwtProvider.getMemberId(token)).isEqualTo(memberId);
    }
    @Test
    void 로그인_비밀번호가_틀리면_401을_반환한다() throws Exception{
        mockMvc.perform(post("/api/members/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                            {"email": "a@a.com", "password": "qwer1234!"}
                            """));

        mockMvc.perform(post("/api/members/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {"email": "a@a.com", "password": "asdf1234!"}
                            """))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value(ErrorCode.AUTHENTICATION_FAILED.name()));
    }

    @Test
    void 로그인_이메일이_존재하지_않으면_401을_반환한다() throws Exception{
        mockMvc.perform(post("/api/members/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {"email": "a@a.com", "password": "asdf1234!"}
                            """))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value(ErrorCode.AUTHENTICATION_FAILED.name()));
    }

}