package shopping.product;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ProductNameTest {

    @ParameterizedTest
    @ValueSource(strings = {"아이스 아메리카노", "콜라(500ml)", "[특가] 사과+배", "A&B/C_D-E"})
    void 허용된_문자로_된_이름은_만들_수_있다(String name) {
        assertThat(new ProductName(name).getValue()).isEqualTo(name);
    }

    @Test
    void 공백_포함_15자까지_허용한다() {
        String name = "가나다라마 바사아자차 카타파";
        assertThat(new ProductName(name).getValue()).hasSize(15);
    }

    @Test
    void 이름이_15자를_넘으면_예외() {
        assertThatThrownBy(() -> new ProductName("가나다라마바사아자차카타파하가나"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("15자");
    }

    @ParameterizedTest
    @ValueSource(strings = {"사과!", "배#", "포도*", "귤@"})
    void 허용되지_않은_특수_문자가_있으면_예외(String name) {
        assertThatThrownBy(() -> new ProductName(name))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   "})
    void 비어_있으면_예외(String name) {
        assertThatThrownBy(() -> new ProductName(name))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
