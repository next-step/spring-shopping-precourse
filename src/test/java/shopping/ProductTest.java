package shopping;

import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class ProductTest {
    private final ProductNameFilter filter = new DefaultProductNameFilter(List.of("금칙어"));

    @Test
    void 이름은_공백일_수_없다() {
        Assertions.assertThatThrownBy(() -> new Product(1L, " ", 1000L, "www.example.com", filter))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 특수_문자_잘못된_거_포함할_수_없다() {
        Assertions.assertThatThrownBy(() -> new Product(1L, "치이카와!@#$%^", 1000L, "www.example.com", filter))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 이름_15자_초과_예외() {
        Assertions.assertThatThrownBy(() -> new Product(1L, "abcdefghijklmnop", 1000L, "www.example.com", filter))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 이름_15자_정상() {
        Assertions.assertThatNoException().isThrownBy(() -> new Product(1L, "abcdefghijklmno", 1000L, "www.example.com", filter));
    }

    @Test
    void 무료_상품의_가격은_0원이다() {
        var product = new Product(1L, "치이카와", 0L, "www.example.com", filter);

        Assertions.assertThat(product.price()).isZero();
    }

    @Test
    void 상품_가격은_음수일_수_없다() {
        Assertions.assertThatThrownBy(() -> new Product(1L, "치이카와", -1L, "www.example.com", filter))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 이름에_비속어가_있으면_상품을_생성할_수_없다() {
        Assertions.assertThatThrownBy(
                () -> new Product(1L, "금칙어인형", 1000L, "www.example.com", filter))
                .isInstanceOf(IllegalArgumentException.class);
    }

    // 양수 가격이 입력한 값 그대로 저장되는지도 테스트할 수 있다. 생략.
}
