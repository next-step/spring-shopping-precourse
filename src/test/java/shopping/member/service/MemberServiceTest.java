package shopping.member.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.password.PasswordEncoder;
import shopping.auth.AuthConfig;
import shopping.auth.JwtTokenProvider;
import shopping.member.domain.Member;
import shopping.member.exception.DuplicateEmailException;
import shopping.member.exception.InvalidCredentialsException;
import shopping.member.repository.MemberRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@Import({MemberService.class, JwtTokenProvider.class, AuthConfig.class})
class MemberServiceTest {
    private static final String EMAIL = "a@a.com";
    private static final String PASSWORD = "1234";

    @Autowired
    private MemberService memberService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Test
    void 가입하면_회원이_저장된다() {
        Member member = memberService.register(EMAIL, PASSWORD);

        assertThat(member.getId()).isNotNull();
        assertThat(memberRepository.findByEmail(EMAIL)).isPresent();
    }

    @Test
    void 비밀번호는_해시되어_저장된다() {
        Member member = memberService.register(EMAIL, PASSWORD);

        assertThat(member.getPassword()).isNotEqualTo(PASSWORD);
        assertThat(passwordEncoder.matches(PASSWORD, member.getPassword())).isTrue();
    }

    @Test
    void 이미_가입된_이메일이면_예외가_발생하고_저장되지_않는다() {
        memberService.register(EMAIL, PASSWORD);
        assertThatThrownBy(() -> memberService.register(EMAIL, "124234"))
                .isInstanceOf(DuplicateEmailException.class);
        assertThat(memberRepository.findAll()).hasSize(1);
    }

    @Test
    void 로그인하면_회원_id가_담긴_토큰을_돌려준다() {
        Member member = memberService.register(EMAIL, PASSWORD);

        String token = memberService.login(EMAIL, PASSWORD);

        assertThat(jwtTokenProvider.extractMemberId(token)).isEqualTo(member.getId());
    }

    @Test
    void 비밀번호가_틀리면_로그인에_실패한다() {
        memberService.register(EMAIL, PASSWORD);

        assertThatThrownBy(() -> memberService.login(EMAIL, "wrong"))
                .isInstanceOf(InvalidCredentialsException.class);
    }

    @Test
    void 가입되지_않은_이메일이면_로그인에_실패한다() {
        assertThatThrownBy(() -> memberService.login("none@a.com", PASSWORD))
                .isInstanceOf(InvalidCredentialsException.class);
    }
}
