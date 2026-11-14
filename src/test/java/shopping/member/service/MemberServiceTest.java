package shopping.member.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import shopping.auth.JwtProvider;
import shopping.member.dto.MemberLoginRequest;
import shopping.member.dto.MemberRegisterRequest;
import shopping.member.dto.MemberRegisterResponse;
import shopping.member.dto.TokenResponse;
import shopping.member.entity.Member;
import shopping.member.exception.AuthenticationFailedException;
import shopping.member.exception.DuplicateEmailException;
import shopping.member.repository.MemberRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class MemberServiceTest {

    @Autowired MemberService memberService;
    @Autowired JwtProvider jwtProvider;
    @Autowired private MemberRepository memberRepository;

    @Test
    void 중복없는_이메일로_회원가입() {
        MemberRegisterRequest request = new MemberRegisterRequest("a@a.com", "qwer1234!");
        TokenResponse response = memberService.register(request);
        Member member = memberRepository.findByEmail("a@a.com").orElseThrow();
        assertThat(jwtProvider.getMemberId(response.token())).isEqualTo(member.getId());
    }

    @Test
    void 중복_이메일_회원가입(){
        MemberRegisterRequest request1 = new MemberRegisterRequest("a@a.com", "qwer1234!");
        MemberRegisterRequest request2 = new MemberRegisterRequest("a@a.com", "wasd1234!");

        // 미리 가입 시키기
        memberService.register(request1);

        //중복 이메일 회원가입 시도
        assertThatThrownBy(()->memberService.register(request2))
                .isInstanceOf(DuplicateEmailException.class)
                .hasMessage("이미 가입된 계정입니다.");
    }

    @Test
    void 정상_로그인() {
        MemberRegisterRequest memberRegisterRequest = new MemberRegisterRequest("a@a.com", "qwer1234!");
        memberService.register(memberRegisterRequest);

        MemberLoginRequest memberLoginRequest = new MemberLoginRequest("a@a.com", "qwer1234!");
        TokenResponse tokenResponse = memberService.login(memberLoginRequest);

        Member member = memberRepository.findByEmail(memberLoginRequest.email()).orElseThrow();
        assertThat(jwtProvider.getMemberId(tokenResponse.token())).isEqualTo(member.getId());
    }

    @Test
    void 존재하지_않는_계정으로_로그인(){
        MemberLoginRequest memberLoginRequest = new MemberLoginRequest("a@a.com", "qwer1234!");
        assertThatThrownBy(()->memberService.login(memberLoginRequest))
                .isInstanceOf(AuthenticationFailedException.class)
                .hasMessage("이메일 또는 비밀번호가 올바르지 않습니다.");
    }
}