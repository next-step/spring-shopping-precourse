package shopping.member;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import shopping.auth.JwtTokenProvider;

class MemberServiceTest {

    private static final String EMAIL = "hong@example.com";
    private static final String PASSWORD = "1234";

    private final JwtTokenProvider tokenProvider =
            new JwtTokenProvider("test-secret-key-for-jwt-test-1234567890", 3_600_000);
    private final MemberService service = new MemberService(new MemberRepository(), tokenProvider);

    @Test
    void 가입하면_해당_이메일의_토큰이_발급된다() {
        String token = service.register(EMAIL, PASSWORD);

        assertThat(tokenProvider.extractEmail(token)).isEqualTo(EMAIL);
    }

    @Test
    void 이미_가입된_이메일이면_예외가_발생한다() {
        service.register(EMAIL, PASSWORD);

        assertThatThrownBy(() -> service.register(EMAIL, "other"))
                .isInstanceOf(DuplicateEmailException.class);
    }

    @Test
    void 올바른_정보로_로그인하면_토큰이_발급된다() {
        service.register(EMAIL, PASSWORD);

        String token = service.login(EMAIL, PASSWORD);

        assertThat(tokenProvider.extractEmail(token)).isEqualTo(EMAIL);
    }

    @Test
    void 비밀번호가_틀리면_로그인에_실패한다() {
        service.register(EMAIL, PASSWORD);

        assertThatThrownBy(() -> service.login(EMAIL, "wrong"))
                .isInstanceOf(LoginFailedException.class);
    }

    @Test
    void 가입되지_않은_이메일로_로그인하면_실패한다() {
        assertThatThrownBy(() -> service.login("none@example.com", PASSWORD))
                .isInstanceOf(LoginFailedException.class);
    }

    @Test
    void 이메일이_없는_로그인_요청은_실패한다() {
        assertThatThrownBy(() -> service.login(null, PASSWORD))
                .isInstanceOf(LoginFailedException.class);
    }
}