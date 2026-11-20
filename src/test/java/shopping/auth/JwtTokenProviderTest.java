package shopping.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class JwtTokenProviderTest {

    private static final String SECRET = "test-secret-key-for-jwt-test-1234567890";
    private static final String OTHER_SECRET = "another-secret-key-for-jwt-test-12345678";
    private static final String EMAIL = "hong@example.com";

    private final JwtTokenProvider provider = new JwtTokenProvider(SECRET, 3_600_000);

    @Test
    void 발급한_토큰에서_이메일을_꺼낼_수_있다() {
        String token = provider.createToken(EMAIL);

        assertThat(provider.extractEmail(token)).isEqualTo(EMAIL);
    }

    @Test
    void 다른_키로_서명된_토큰은_예외가_발생한다() {
        String forged = new JwtTokenProvider(OTHER_SECRET, 3_600_000).createToken(EMAIL);

        assertThatThrownBy(() -> provider.extractEmail(forged))
                .isInstanceOf(InvalidTokenException.class);
    }

    @Test
    void 만료된_토큰은_예외가_발생한다() {
        String expired = new JwtTokenProvider(SECRET, -1000).createToken(EMAIL);

        assertThatThrownBy(() -> provider.extractEmail(expired))
                .isInstanceOf(InvalidTokenException.class);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"abc", "a.b.c"})
    void 형식이_잘못된_토큰은_예외가_발생한다(String token) {
        assertThatThrownBy(() -> provider.extractEmail(token))
                .isInstanceOf(InvalidTokenException.class);
    }
}