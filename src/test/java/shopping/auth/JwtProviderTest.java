package shopping.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.WeakKeyException;
import org.junit.jupiter.api.Test;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JwtProviderTest {

    private static final String SECRET = UUID.randomUUID().toString();
    private static final SecretKey KEY =
            Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
    private final JwtProvider jwtProvider = new JwtProvider(SECRET, 30);

    private Claims parse(String token) {
        return Jwts.parser()
                .verifyWith(KEY)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    @Test
    void 토큰의_sub에_회원_id가_담긴다() {
        // when
        Long memberId = 1L;

        // then
        String token = jwtProvider.createToken(memberId);
        assertThat(parse(token).getSubject()).isEqualTo(String.valueOf(memberId));
    }

    @Test
    void 만료_시각은_발급_시각의_30분_뒤다() {
        // when
        String token = jwtProvider.createToken(1L);

        // then
        Claims claims = parse(token);
        assertThat(claims.getExpiration().getTime() - claims.getIssuedAt().getTime())
                .isEqualTo(30 * 60 * 1000L);
    }

    @Test
    void 키가_32바이트보다_짧으면_거절한다() {
        // given
        String shortSecret = "a".repeat(31);

        // when & then
        assertThatThrownBy(() -> new JwtProvider(shortSecret, 30))
                .isInstanceOf(WeakKeyException.class);
    }
}