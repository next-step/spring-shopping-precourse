package shopping.product;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class PriceTest {
    @ParameterizedTest 
    @ValueSource(ints = {0, 1, 1000, 10000, 100000})
    void 가격이_0이상이어야한다(int value) {
        Price price = new Price(value);

        assertThat(price.getValue()).isEqualTo(value);
    }

    @ParameterizedTest 
    @ValueSource(ints = {-1, -1000, -10000, -100000})
    void 가격이_0미만이면_예외가_발생한다(int value) {
        assertThatThrownBy(() -> new Price(value))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("가격은 0 이상이어야 합니다.");
    }
}
