package shopping.product;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class ProductTest {

    private static final String IMAGE = "https://example.com/a.png";

    @Test
    void 정상_값으로_상품이_생성된다() {
        Product product = new Product(null, new ProductName("사과"), 1000, IMAGE);

        assertThat(product.getPrice()).isEqualTo(1000);
    }

    @Test
    void 가격이_음수이면_예외가_발생한다() {
        assertThatThrownBy(() -> new Product(null, new ProductName("사과"), -1, IMAGE))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"example.com/a.png", "ftp://example.com/a.png"})
    void 이미지_URL이_올바르지_않으면_예외가_발생한다(String url) {
        assertThatThrownBy(() -> new Product(null, new ProductName("사과"), 1000, url))
                .isInstanceOf(IllegalArgumentException.class);
    }
}