package shopping.product.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.assertThatCode;

public class ProductNameTest {

    @Test
    void 이름에_허용된_특수문자가_들어가면_예외가_발생하지_않는다() {
        assertThatCode(() -> new ProductName("가나다"))
                .doesNotThrowAnyException();
    }

    @Test
    void 이름에_허용되지_않는_특수문자가_들어가면_예외가_발생한다() {
        assertThatThrownBy(() -> new ProductName("가나다><"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 이름이_15자를_넘으면_예외가_발생한다() {
        assertThatThrownBy(() -> new ProductName("가".repeat(16)))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 이름이_15자면_생성된다() {
        assertThatCode(() -> new ProductName("가".repeat(15)))
                .doesNotThrowAnyException();
    }
}
