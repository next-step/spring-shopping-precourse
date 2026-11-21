package shopping.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import javax.crypto.SecretKey;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import shopping.auth.exception.InvalidTokenException;

class JwtTokenProviderTest {

    private static final String SECRET_KEY = "test-secret-key-test-secret-key-test-secret-key";

    private final JwtTokenProvider jwtTokenProvider = new JwtTokenProvider(SECRET_KEY, 3_600_000L);

    @Test
    @DisplayName("회원 ID를 subject로 담은 토큰을 만든다")
    void createToken() {
        String token = jwtTokenProvider.createToken(1L);

        // 같은 비밀 키로 토큰을 직접 풀어서 subject에 회원 ID가 들어 있는지 확인한다.
        SecretKey key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));
        String subject = Jwts.parser().verifyWith(key).build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
        assertThat(subject).isEqualTo("1");
    }

    @Test
    @DisplayName("토큰에서 회원 ID를 꺼낸다")
    void getMemberId() {
        String token = jwtTokenProvider.createToken(1L);

        assertThat(jwtTokenProvider.getMemberId(token)).isEqualTo(1L);
    }

    @Test
    @DisplayName("직접 발급한 토큰은 유효하다")
    void validToken() {
        String token = jwtTokenProvider.createToken(1L);

        assertThat(jwtTokenProvider.isValidToken(token)).isTrue();
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "invalid.token.value"})
    @DisplayName("토큰이 없거나 형식이 잘못되면 유효하지 않다")
    void malformedToken(String token) {
        assertThat(jwtTokenProvider.isValidToken(token)).isFalse();
    }

    @Test
    @DisplayName("다른 비밀 키로 서명한 토큰은 유효하지 않다")
    void tokenSignedWithOtherKey() {
        JwtTokenProvider other =
                new JwtTokenProvider("other-secret-key-other-secret-key-other", 3_600_000L);
        String token = other.createToken(1L);

        assertThat(jwtTokenProvider.isValidToken(token)).isFalse();
    }

    @Test
    @DisplayName("만료된 토큰은 유효하지 않다")
    void expiredToken() {
        // 만료 시간을 음수로 주면 발급하자마자 만료된 토큰이 만들어진다.
        JwtTokenProvider expired = new JwtTokenProvider(SECRET_KEY, -1_000L);
        String token = expired.createToken(1L);

        assertThat(jwtTokenProvider.isValidToken(token)).isFalse();
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "invalid.token.value"})
    @DisplayName("토큰이 없거나 형식이 잘못되면 회원 ID를 꺼낼 때 예외가 발생한다")
    void getMemberIdWithMalformedToken(String token) {
        assertThatThrownBy(() -> jwtTokenProvider.getMemberId(token))
                .isInstanceOf(InvalidTokenException.class)
                .hasMessage("유효하지 않은 인증 토큰입니다.");
    }

    @Test
    @DisplayName("다른 비밀 키로 서명한 토큰이면 회원 ID를 꺼낼 때 예외가 발생한다")
    void getMemberIdWithTokenSignedWithOtherKey() {
        JwtTokenProvider other =
                new JwtTokenProvider("other-secret-key-other-secret-key-other", 3_600_000L);
        String token = other.createToken(1L);

        assertThatThrownBy(() -> jwtTokenProvider.getMemberId(token))
                .isInstanceOf(InvalidTokenException.class);
    }

    @Test
    @DisplayName("만료된 토큰이면 회원 ID를 꺼낼 때 예외가 발생한다")
    void getMemberIdWithExpiredToken() {
        JwtTokenProvider expired = new JwtTokenProvider(SECRET_KEY, -1_000L);
        String token = expired.createToken(1L);

        assertThatThrownBy(() -> jwtTokenProvider.getMemberId(token))
                .isInstanceOf(InvalidTokenException.class);
    }
}
