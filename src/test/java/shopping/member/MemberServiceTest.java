package shopping.member;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import shopping.auth.JwtTokenProvider;
import shopping.common.DuplicateException;
import shopping.common.UnauthorizedException;
import shopping.member.dto.MemberRequest;
import shopping.member.dto.TokenResponse;

@SpringBootTest
@Transactional
class MemberServiceTest {

    private static final MemberRequest MEMBER = new MemberRequest("test@test.com", "1234");
    private static final MemberRequest WRONG_PASSWORD = new MemberRequest("test@test.com", "9999");
    private static final MemberRequest NO_MEMBER = new MemberRequest("nobody@test.com", "1234");

    @Autowired
    private MemberService memberService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Test
    @DisplayName("가입하면 그 회원의 토큰을 받는다")
    void registersMember() {
        TokenResponse response = memberService.register(MEMBER);
        long id = jwtTokenProvider.getMemberId(response.token());
        Member member = memberRepository.findById(id).orElseThrow();
        assertThat(member.getEmail()).isEqualTo("test@test.com");
    }

    @Test
    @DisplayName("이미 가입된 이메일로 가입하면 예외가 발생한다")
    void throwsWhenDuplicate() {
        memberService.register(MEMBER);
        assertThatThrownBy(() -> memberService.register(MEMBER))
                .isInstanceOf(DuplicateException.class);
    }

    @Test
    @DisplayName("이메일 형식이 틀리면 가입할 수 없다")
    void throwsWhenInvalidEmail() {
        MemberRequest request = new MemberRequest("testtest.com", "1234");
        assertThatThrownBy(() -> memberService.register(request))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("가입한 이메일과 비밀번호로 로그인하면 토큰을 받는다")
    void logsIn() {
        memberService.register(MEMBER);
        TokenResponse response = memberService.login(MEMBER);
        assertThat(response.token()).isNotBlank();
    }

    @Test
    @DisplayName("비밀번호가 틀리면 로그인할 수 없다")
    void throwsWhenWrongPassword() {
        memberService.register(MEMBER);
        assertThatThrownBy(() -> memberService.login(WRONG_PASSWORD))
                .isInstanceOf(UnauthorizedException.class);
    }

    @Test
    @DisplayName("가입하지 않은 이메일로는 로그인할 수 없다")
    void throwsWhenNoMember() {
        assertThatThrownBy(() -> memberService.login(NO_MEMBER))
                .isInstanceOf(UnauthorizedException.class);
    }
}