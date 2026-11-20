package shopping.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.Test;

class JwtTokenProviderTest {
    private static final String SECRET = "test-secret-key-must-be-at-least-32-bytes-long!!";
    private static final String OTHER_SECRET = "other-secret-key-must-be-at-least-32-bytes-long!";
    private static final long ONE_HOUR = 3_600_000L;

    private final JwtTokenProvider provider = new JwtTokenProvider(SECRET, ONE_HOUR);

    @Test
    void 만든_토큰에서_회원_id를_꺼낼_수_있다() {
        String token = provider.create(7L);

        assertThat(provider.extractMemberId(token)).isEqualTo(7L);
    }

    @Test
    void 다른_키로_만든_토큰은_거부한다() {
        String token = new JwtTokenProvider(OTHER_SECRET, ONE_HOUR).create(7L);

        assertThatThrownBy(() -> provider.extractMemberId(token))
                .isInstanceOf(JwtException.class);
    }

    @Test
    void 만료된_토큰은_거부한다() {
        String token = new JwtTokenProvider(SECRET, -1000L).create(7L);

        assertThatThrownBy(() -> provider.extractMemberId(token))
                .isInstanceOf(ExpiredJwtException.class);
    }

    @Test
    void 서명이_변조된_토큰은_거부한다() {
        String token = provider.create(7L);
        String tampered = token.substring(0, token.lastIndexOf('.') + 1) + "invalidsignature";

        assertThatThrownBy(() -> provider.extractMemberId(tampered))
                .isInstanceOf(JwtException.class);
    }
}
