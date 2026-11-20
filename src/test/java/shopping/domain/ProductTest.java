package shopping.domain;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatNoException;

class ProductTest {

    @Test
    void 상품이_정상적으로_생성된다() {
        Product product = new Product(1L, "상품", 1000, "http://image.png");

        assertThat(product.id()).isEqualTo(1L);
        assertThat(product.name()).isEqualTo("상품");
        assertThat(product.price()).isEqualTo(1000);
        assertThat(product.imageUrl()).isEqualTo("http://image.png");
    }

    @ParameterizedTest
    @ValueSource(strings = {"a", "상품 이름", "hxeyexnhxeyexnh"})
    void 상품_이름이_15자_이하이면_상품이_생성된다(String name) {
        assertThatNoException()
                .isThrownBy(() -> new Product(1L, name, 1000, "http://image.png"));
    }

    @Test
    void 상품_이름이_15자를_초과하면_상품을_생성하지_않는다() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> new Product(1L, "hxeyexnhxeyexnhxeyexn", 1000, "http://image.png"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"상품(A)", "상품[B]", "A+B", "A-B", "A&B", "A/B", "A_B"})
    void 상품_이름에_허용된_특수_문자가_포함되면_상품이_생성된다(String name) {
        assertThatNoException()
                .isThrownBy(() -> new Product(1L, name, 1000, "http://image.png"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"상품!", "상품@", "상품#", "상품%", "상품*"})
    void 상품_이름에_허용되지_않은_특수_문자가_포함되면_상품을_생성하지_않는다(String name) {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> new Product(1L, name, 1000, "http://image.png"));
    }

    @Test
    void 상품_이름이_비어있으면_상품을_생성하지_않는다() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> new Product(1L, null, 1000, "http://image.png"));
    }
}
