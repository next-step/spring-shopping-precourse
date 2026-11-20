package shopping.product.domain;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import shopping.product.exception.InvalidProductNameException;

public class ProductNameValidatorTest {
    private final ProductNameValidator validator = new ProductNameValidator();

    @Test
    void 이름이_15자면_통과한다() {
        assertThatCode(() -> validator.validate("a".repeat(15)))
                .doesNotThrowAnyException();
    }

    @Test
    void 이름이_16자면_예외가_난다() {
        assertThatThrownBy(() -> validator.validate("a".repeat(16)))
                .isInstanceOf(InvalidProductNameException.class);
    }

    @Test
    void 허용된_특수문자가_들어_있어도_통과한다() {
        assertThatCode(() -> validator.validate("컵(1)[2]+-&/_"))
                .doesNotThrowAnyException();
    }

    @ParameterizedTest
    @ValueSource(strings = {"컵!", "컵@", "컵#", "컵$", "컵%"})
    void 허용되지_않은_특수문자가_있으면_예외가_난다(String name) {
        assertThatThrownBy(() -> validator.validate(name))
                .isInstanceOf(InvalidProductNameException.class);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "   "})
    void 이름이_비어_있으면_예외가_난다(String name) {
        assertThatThrownBy(() -> validator.validate(name))
                .isInstanceOf(InvalidProductNameException.class);
    }
}
