package shopping;

import java.util.Map;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class AuthenticationTest {
    private final Email email = new Email("tom", new Domain("gmail.com"));
    private final MemberRepository members = new FakeMemberRepository(Map.of(), 1L);
    private final PasswordEncoder encoder = new PlainPasswordEncoder();
    private final Registration registration = new Registration(members, encoder);
    private final Login login = new Login(members, encoder);
    private final TokenRepository tokens = new InMemoryTokenRepository();
    private final Authentication authentication = new Authentication(registration, login, tokens);

    @Test
    void 가입_토큰으로_회원_ID를_조회한다() {
        String token = authentication.registrationToken(email, "0123456789");

        Assertions.assertThat(tokens.memberId(token)).isEqualTo(1L);
    }

    @Test
    void 로그인_토큰으로_회원_ID를_조회한다() {
        Member member = registration.member(email, "0123456789");

        String token = authentication.loginToken(email, "0123456789");

        Assertions.assertThat(tokens.memberId(token)).isEqualTo(member.id());
    }

    @Test
    void 비밀번호가_틀리면_토큰을_발급하지_않는다() {
        registration.member(email, "0123456789");
        var authentication = new Authentication(registration, login, new UnusedTokenRepository());

        Assertions.assertThatThrownBy(() -> authentication.loginToken(email, "abcdefghij"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    // 발급된 적 없는 토큰으로 조회할 수 없는지도 테스트할 수 있다. 생략.

    private static final class UnusedTokenRepository implements TokenRepository {
        @Override
        public String token(Long memberId) {
            throw new AssertionError("토큰을 발급하면 안 된다");
        }

        @Override
        public Long memberId(String token) {
            throw new AssertionError("토큰을 조회하면 안 된다");
        }
    }
}
