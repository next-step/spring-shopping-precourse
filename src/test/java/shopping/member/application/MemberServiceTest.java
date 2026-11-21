package shopping.member.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import shopping.member.domain.Member;
import shopping.member.domain.MemberRepository;
import shopping.member.dto.MemberRequest;
import shopping.member.dto.TokenResponse;
import shopping.member.exception.DuplicateEmailException;
import shopping.member.exception.LoginFailedException;

@SpringBootTest
@Transactional
class MemberServiceTest {

    private static final MemberRequest REQUEST = new MemberRequest("user@test.com", "password");

    @Autowired
    private MemberService memberService;

    @Autowired
    private MemberRepository memberRepository;

    @Test
    @DisplayName("이메일과 비밀번호로 회원 가입하면 회원이 저장된다")
    void register() {
        memberService.register(REQUEST);

        Member member = memberRepository.findAll().getFirst();
        assertThat(member.getEmail()).isEqualTo("user@test.com");
        assertThat(member.getPassword()).isEqualTo("password");
    }

    @Test
    @DisplayName("회원 가입에 성공하면 인증 토큰이 발급된다")
    void registerReturnsToken() {
        TokenResponse response = memberService.register(REQUEST);

        assertThat(response.token()).isNotBlank();
    }

    @Test
    @DisplayName("이미 가입된 이메일로 회원 가입하면 예외가 발생한다")
    void registerWithDuplicateEmail() {
        memberService.register(REQUEST);
        MemberRequest sameEmail = new MemberRequest("user@test.com", "other-password");

        assertThatThrownBy(() -> memberService.register(sameEmail))
                .isInstanceOf(DuplicateEmailException.class)
                .hasMessage("이미 가입된 이메일입니다.");
    }

    @Test
    @DisplayName("가입한 이메일과 비밀번호로 로그인하면 인증 토큰이 발급된다")
    void login() {
        memberService.register(REQUEST);

        TokenResponse response = memberService.login(REQUEST);

        assertThat(response.token()).isNotBlank();
    }

    @Test
    @DisplayName("가입하지 않은 이메일로 로그인하면 예외가 발생한다")
    void loginWithUnknownEmail() {
        MemberRequest unknown = new MemberRequest("unknown@test.com", "password");

        assertThatThrownBy(() -> memberService.login(unknown))
                .isInstanceOf(LoginFailedException.class)
                .hasMessage("이메일 또는 비밀번호가 일치하지 않습니다.");
    }

    @Test
    @DisplayName("비밀번호가 일치하지 않으면 예외가 발생한다")
    void loginWithWrongPassword() {
        memberService.register(REQUEST);
        MemberRequest wrongPassword = new MemberRequest("user@test.com", "wrong-password");

        assertThatThrownBy(() -> memberService.login(wrongPassword))
                .isInstanceOf(LoginFailedException.class)
                .hasMessage("이메일 또는 비밀번호가 일치하지 않습니다.");
    }
}
