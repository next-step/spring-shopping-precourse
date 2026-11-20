package shopping.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import shopping.common.exception.UnauthorizedException;

class JwtTokenProviderTest {

    private static final String SECRET = "test-secret-key-test-secret-key-test-secret-key";
    private static final String OTHER_SECRET = "other-secret-key-other-secret-key-other-secret";
    private static final long ONE_HOUR = 3_600_000L;

    private final JwtTokenProvider tokenProvider = new JwtTokenProvider(SECRET, ONE_HOUR);

    @Test
    void 토큰을_만들고_다시_회원_id를_꺼낼_수_있다() {
        String token = tokenProvider.createToken(1L);

        assertThat(tokenProvider.extractMemberId(token)).isEqualTo(1L);
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "abc", "aaa.bbb.ccc"})
    void 형식이_잘못된_토큰은_예외가_발생한다(String token) {
        assertThatThrownBy(() -> tokenProvider.extractMemberId(token))
                .isInstanceOf(UnauthorizedException.class);
    }

    @Test
    void 다른_키로_서명한_토큰은_예외가_발생한다() {
        String forged = new JwtTokenProvider(OTHER_SECRET, ONE_HOUR).createToken(1L);

        assertThatThrownBy(() -> tokenProvider.extractMemberId(forged))
                .isInstanceOf(UnauthorizedException.class);
    }

    @Test
    void 만료된_토큰은_예외가_발생한다() {
        String expired = new JwtTokenProvider(SECRET, -1000L).createToken(1L);

        assertThatThrownBy(() -> tokenProvider.extractMemberId(expired))
                .isInstanceOf(UnauthorizedException.class);
    }
}
