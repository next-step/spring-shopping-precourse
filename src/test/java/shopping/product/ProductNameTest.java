package shopping.product;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;

class ProductNameTest {

    @ParameterizedTest
    @ValueSource(strings = {"사과", "사과(유기농)", "A+B [세트]", "a-b&c/d_e", "りんご"})
    void 허용된_이름은_생성된다(String name) {
        assertThat(new ProductName(name).getValue()).isEqualTo(name);
    }

    @Test
    void 공백_포함_15자는_허용된다() {
        String name = "가".repeat(14) + " ";

        assertThat(new ProductName(name).getValue()).hasSize(15);
    }

    @Test
    void 이름이_16자이면_예외가_발생한다() {
        assertThatThrownBy(() -> new ProductName("가".repeat(16)))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @ParameterizedTest
    @ValueSource(strings = {"사과@", "사과!", "사과#", "사과%", "사과*", "사과."})
    void 허용되지_않은_특수문자는_예외가_발생한다(String name) {
        assertThatThrownBy(() -> new ProductName(name))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   "})
    void 빈_이름은_예외가_발생한다(String name) {
        assertThatThrownBy(() -> new ProductName(name))
                .isInstanceOf(IllegalArgumentException.class);
    }
}