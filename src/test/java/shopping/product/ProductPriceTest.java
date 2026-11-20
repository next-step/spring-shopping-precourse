package shopping.product;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class ProductPriceTest {

    @Test
    @DisplayName("1원은 만들어진다")
    void createsValidPrice() {
        ProductPrice price = new ProductPrice(1);
        assertThat(price.getValue()).isEqualTo(1);
    }

    @Test
    @DisplayName("0원이면 예외가 발생한다")
    void throwsWhenZero() {
        assertThatThrownBy(() -> new ProductPrice(0))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("음수면 예외가 발생한다")
    void throwsWhenNegative() {
        assertThatThrownBy(() -> new ProductPrice(-100))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
