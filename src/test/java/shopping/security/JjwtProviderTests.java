package shopping.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import shopping.exception.InvalidTokenException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JjwtProviderTests {

    private static final String SECRET = "test-secret-key-for-jwt-signing-1234567890";
    private static final String OTHER_SECRET = "other-secret-key-for-jwt-signing-0987654321";
    private static final long ONE_HOUR = 3_600_000L;

    private final JjwtProvider jwtProvider = new JjwtProvider(SECRET, ONE_HOUR);

    @DisplayName("토큰은 헤더, 페이로드, 서명 세 부분으로 이루어진 JWT 형식이다")
    @Test
    void createTokenHasJwtFormat() {
        String token = jwtProvider.createToken(1L);

        assertThat(token).isNotBlank();
        assertThat(token.split("\\.")).hasSize(3);
    }

    @DisplayName("발급한 토큰에서 회원 id를 꺼낼 수 있다")
    @Test
    void getMemberIdFromToken() {
        String token = jwtProvider.createToken(1L);

        assertThat(jwtProvider.getMemberId(token)).isEqualTo(1L);
    }

    @DisplayName("토큰 만료까지 남은 시간을 초 단위로 반환한다")
    @Test
    void expiresInSeconds() {
        assertThat(jwtProvider.getExpiresInSeconds()).isEqualTo(3600L);
    }

    @DisplayName("회원마다 서로 다른 토큰이 발급된다")
    @Test
    void tokensDifferByMember() {
        String first = jwtProvider.createToken(1L);
        String second = jwtProvider.createToken(2L);

        assertThat(first).isNotEqualTo(second);
        assertThat(jwtProvider.getMemberId(second)).isEqualTo(2L);
    }

    @DisplayName("만료된 토큰이면 예외가 발생한다")
    @Test
    void expiredToken() {
        JjwtProvider expiredProvider = new JjwtProvider(SECRET, -1_000L);
        String token = expiredProvider.createToken(1L);

        assertThatThrownBy(() -> expiredProvider.getMemberId(token))
                .isInstanceOf(InvalidTokenException.class);
    }

    @DisplayName("페이로드를 바꿔치기한 토큰이면 예외가 발생한다")
    @Test
    void tamperedToken() {
        String[] first = jwtProvider.createToken(1L).split("\\.");
        String[] second = jwtProvider.createToken(2L).split("\\.");
        String tampered = first[0] + "." + second[1] + "." + first[2];

        assertThatThrownBy(() -> jwtProvider.getMemberId(tampered))
                .isInstanceOf(InvalidTokenException.class);
    }

    @DisplayName("다른 비밀 키로 서명한 토큰이면 예외가 발생한다")
    @Test
    void tokenSignedWithOtherSecret() {
        String token = new JjwtProvider(OTHER_SECRET, ONE_HOUR).createToken(1L);

        assertThatThrownBy(() -> jwtProvider.getMemberId(token))
                .isInstanceOf(InvalidTokenException.class);
    }

    @DisplayName("JWT 형식이 아니거나 비어 있는 토큰이면 예외가 발생한다")
    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "not-a-jwt", "a.b.c"})
    void malformedToken(String token) {
        assertThatThrownBy(() -> jwtProvider.getMemberId(token))
                .isInstanceOf(InvalidTokenException.class);
    }
}
