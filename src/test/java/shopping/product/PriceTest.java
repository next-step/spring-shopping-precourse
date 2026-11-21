package shopping.product;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class PriceTest {

    @ParameterizedTest
    @ValueSource(ints = {1, 1000})
    @DisplayName("상품 가격은 1원 이상이면 허용한다")
    void acceptPositivePrice(int value) {
        Price price = new Price(value);

        assertThat(price.value()).isEqualTo(value);
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1, -1000})
    @DisplayName("상품 가격이 0원 이하이면 거부한다")
    void rejectZeroOrNegativePrice(int value) {
        assertThatThrownBy(() -> new Price(value))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("상품 가격은 1원 이상이어야 합니다.");
    }
}
