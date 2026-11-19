package shopping.product.domain;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

public class ProductPriceTest {

    @Test
    void 가격이_0보다_작으면_예외가_발생한다() {
        assertThatThrownBy(() -> new ProductPrice(-2))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 가격이_null이면_예외가_발생한다() {
        assertThatThrownBy(() -> new ProductPrice(null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 가격이_0보다_크면_예외가_발생하지_않는다() {
        assertThatCode(() -> new ProductPrice(4))
                .doesNotThrowAnyException();
    }

    @Test
    void 가격이_0이면_예외가_발생한다() {
        assertThatThrownBy(() -> new ProductPrice(0))
                .isInstanceOf(IllegalArgumentException.class);
    }


}
