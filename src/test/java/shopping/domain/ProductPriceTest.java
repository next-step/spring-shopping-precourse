package shopping.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class ProductPriceTest {
    @ParameterizedTest
    @ValueSource(longs = {-1, -1000, Long.MIN_VALUE})
    @DisplayName("음수 가격은 예외가 발생한다")
    void createNegative(long value) {
        assertThatThrownBy(() -> new ProductPrice(value))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("0원은 허용한다")
    void createZero() {
        ProductPrice price = new ProductPrice(0);

        assertThat(price.value()).isZero();
    }

    @ParameterizedTest
    @ValueSource(longs = {1, 1000, Long.MAX_VALUE})
    @DisplayName("양수 가격은 생성할 수 있다")
    void createPositive(long value) {
        ProductPrice price = new ProductPrice(value);

        assertThat(price.value()).isEqualTo(value);
    }
}
