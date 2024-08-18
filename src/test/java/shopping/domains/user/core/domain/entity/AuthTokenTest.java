package shopping.domains.user.core.domain.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

public class AuthTokenTest {
    @Test
    @DisplayName("토큰 생성 테스트")
    void constructorTest() {
        assertThatNoException().isThrownBy(() -> AuthToken.builder()
                .accessToken("accessToken")
                .refreshToken("refreshToken")
                .build());
    }

    @Test
    @DisplayName("토큰값이 같으면 예외가 발생한다.")
    void constructorExceptionTest() {
        assertThatIllegalArgumentException().isThrownBy(() -> AuthToken.builder()
                .accessToken("accessToken")
                .refreshToken("accessToken")
                .build());
    }

    @Test
    @DisplayName("토큰 생성 NPE 테스트")
    void constructorNPETest() {
        assertThatNullPointerException().isThrownBy(() -> AuthToken.builder()
                .accessToken(null)
                .refreshToken("refreshToken")
                .build());
    }
}
