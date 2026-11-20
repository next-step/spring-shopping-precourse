package shopping.product.util;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ProfanityCheckerTest {
    private final ProfanityChecker profanityChecker = new ProfanityChecker();

    @Test
    @DisplayName("상품 이름에 비속어가 포함되어 있으면 예외가 발생한다")
    void throwsWhenNameContainsProfanity() {
        assertThatThrownBy(() -> profanityChecker.validate("shit coffee"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("상품 이름에 비속어가 포함되어 있습니다.");
    }
}
