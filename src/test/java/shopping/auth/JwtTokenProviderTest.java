package shopping.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import shopping.common.UnauthorizedException;

class JwtTokenProviderTest {

    private static final String SECRET = "shopping-test-secret-key-must-be-32-bytes";
    private static final String OTHER_SECRET = "other-test-secret-key-must-be-32-bytes!!";
    private static final long HOUR = 3600000;

    @Test
    @DisplayName("만든 토큰에서 회원 번호를 다시 꺼낼 수 있다")
    void readsMemberIdFromToken() {
        JwtTokenProvider provider = new JwtTokenProvider(SECRET, HOUR);
        String token = provider.createToken(7L);
        assertThat(provider.getMemberId(token)).isEqualTo(7L);
    }

    @Test
    @DisplayName("가짜 토큰이면 예외가 발생한다")
    void throwsWhenFake() {
        JwtTokenProvider provider = new JwtTokenProvider(SECRET, HOUR);
        assertThatThrownBy(() -> provider.getMemberId("fake.token.value"))
                .isInstanceOf(UnauthorizedException.class);
    }

    @Test
    @DisplayName("만료된 토큰이면 예외가 발생한다")
    void throwsWhenExpired() {
        JwtTokenProvider provider = new JwtTokenProvider(SECRET, -1000);
        String token = provider.createToken(7L);
        assertThatThrownBy(() -> provider.getMemberId(token))
                .isInstanceOf(UnauthorizedException.class);
    }

    @Test
    @DisplayName("다른 비밀키로 만든 토큰이면 예외가 발생한다")
    void throwsWhenOtherKey() {
        JwtTokenProvider other = new JwtTokenProvider(OTHER_SECRET, HOUR);
        JwtTokenProvider provider = new JwtTokenProvider(SECRET, HOUR);
        String token = other.createToken(7L);
        assertThatThrownBy(() -> provider.getMemberId(token))
                .isInstanceOf(UnauthorizedException.class);
    }
}