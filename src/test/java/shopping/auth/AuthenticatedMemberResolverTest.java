package shopping.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import shopping.auth.exception.InvalidTokenException;

class AuthenticatedMemberResolverTest {

    private static final String SECRET_KEY = "test-secret-key-test-secret-key-test-secret-key";

    private final JwtTokenProvider tokenProvider = new JwtTokenProvider(SECRET_KEY, 3_600_000L);
    private final AuthenticatedMemberResolver resolver =
            new AuthenticatedMemberResolver(tokenProvider);

    @Test
    @DisplayName("Bearer 인증 헤더에서 회원 ID를 꺼낸다")
    void resolve() {
        String authorization = "Bearer " + tokenProvider.createToken(1L);

        assertThat(resolver.resolve(authorization)).isEqualTo(1L);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "token", "Basic token", "Bearer ", "Bearer invalid.token"})
    @DisplayName("인증 헤더가 없거나 유효하지 않으면 예외가 발생한다")
    void invalidAuthorization(String authorization) {
        assertThatThrownBy(() -> resolver.resolve(authorization))
                .isInstanceOf(InvalidTokenException.class);
    }
}
