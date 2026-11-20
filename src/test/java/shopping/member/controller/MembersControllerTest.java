package shopping.member.controller;

import static org.assertj.core.api.Assertions.assertThat;
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
import org.springframework.transaction.annotation.Transactional;
import shopping.member.dto.MemberSignUpRequestDto;
import shopping.member.entity.Member;
import shopping.member.repository.MemberRepository;
import shopping.member.service.MemberService;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class MembersControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private MemberService memberService;

    @Test
    @DisplayName("이메일과 비밀번호로 회원가입하면 201과 토큰을 반환하고 회원이 저장된다")
    void signupSuccess() throws Exception {
        String request = """
                {"email": "test@example.com", "password": "password123"}
                """;

        String token = mockMvc.perform(post("/api/members/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Member member = memberRepository.findAll().get(0);
        assertThat(memberRepository.count()).isEqualTo(1);
        assertThat(member.getEmail()).isEqualTo("test@example.com");
        assertThat(token).contains(member.getUuid());
    }

    @Test
    @DisplayName("가입된 이메일과 비밀번호로 로그인하면 200과 토큰을 반환한다")
    void loginSuccess() throws Exception {
        memberService.signup(new MemberSignUpRequestDto("test@test.com", "password123"));
        String request = """
                {"email": "test@test.com", "password": "password123"}
                """;

        String token = mockMvc.perform(post("/api/members/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Member member = memberRepository.findAll().get(0);
        assertThat(member.getEmail()).isEqualTo("test@test.com");
        assertThat(token).contains(member.getUuid());
    }
}
