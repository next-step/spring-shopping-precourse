package shopping.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Base64;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class TokenGeneratorTest {
    private TokenGenerator tokenGenerator;

    @BeforeEach
    void setUp() {
        tokenGenerator = new TokenGenerator();
    }

    @Test
    @DisplayName("생성한 토큰을 디코딩하면 32바이트다")
    void generateThirtyTwoByteToken() {
        byte[] decoded = Base64.getUrlDecoder().decode(tokenGenerator.generate());

        assertThat(decoded).hasSize(32);
    }

    @Test
    @DisplayName("토큰은 Base64 URL 문자로 구성된다")
    void generateUrlSafeToken() {
        assertThat(tokenGenerator.generate())
                .matches("[A-Za-z0-9_-]+");
    }

    @Test
    @DisplayName("연속으로 생성한 토큰은 서로 다르다")
    void generateDifferentTokens() {
        assertThat(tokenGenerator.generate())
                .isNotEqualTo(tokenGenerator.generate());
    }
}
