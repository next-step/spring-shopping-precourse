package shopping;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class ProductTest {

    @Test
    void 이름은_공백일_수_없다() {
        Assertions.assertThatThrownBy(() -> new Product(1L, " ", "www.example.com"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 특수_문자_잘못된_거_포함할_수_없다() {
        Assertions.assertThatThrownBy(() -> new Product(1L, "치이카와!@#$%^", "www.example.com"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 이름_15자_초과_예외() {
        Assertions.assertThatThrownBy(() -> new Product(1L, "abcdefghijklmnop", "www.example.com"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 이름_15자_정상() {
        Assertions.assertThatNoException().isThrownBy(() -> new Product(1L, "abcdefghijklmno", "www.example.com"));
    }
}