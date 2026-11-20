package shopping.product.domain;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

public class ProductNameTest {
    @Test
    void 공백포함16자이상미허용() {
        assertThatThrownBy(() -> new ProductName("1234512345123451"))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @ParameterizedTest
    @ValueSource(strings = {"가방!", "가방@"})
    void 미허용문자포함이름거절(String name) {
        assertThatThrownBy(() -> new ProductName(name))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" "})
    void 널이거나비거나공백인이름은거절(String name) {
        assertThatThrownBy(() -> new ProductName(name))
            .isInstanceOf(IllegalArgumentException.class);
    }

}
