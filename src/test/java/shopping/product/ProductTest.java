package shopping.product;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ProductTest {

    private static final String NAME = "아이스 아메리카노";
    private static final int PRICE = 4500;
    private static final String IMAGE_URL = "https://example.com/coffee.png";

    @Test
    void 올바른_값으로_상품을_만든다() {
        Product product = new Product(NAME, PRICE, IMAGE_URL);

        assertThat(product.getName()).isEqualTo(NAME);
        assertThat(product.getPrice()).isEqualTo(PRICE);
        assertThat(product.getImageUrl()).isEqualTo(IMAGE_URL);
    }

    @Test
    void 가격이_0이면_만들어진다() {
        Product product = new Product(NAME, 0, IMAGE_URL);

        assertThat(product.getPrice()).isZero();
    }

    @Test
    void 가격이_음수면_예외() {
        assertThatThrownBy(() -> new Product(NAME, -1, IMAGE_URL))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 가격이_없으면_예외() {
        assertThatThrownBy(() -> new Product(NAME, null, IMAGE_URL))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @ParameterizedTest
    @ValueSource(strings = {"ftp://example.com/a.png", "example.com/a.png", "   "})
    void 이미지_URL이_http로_시작하지_않으면_예외(String imageUrl) {
        assertThatThrownBy(() -> new Product(NAME, PRICE, imageUrl))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @ParameterizedTest
    @NullAndEmptySource
    void 이미지_URL이_비어_있으면_예외(String imageUrl) {
        assertThatThrownBy(() -> new Product(NAME, PRICE, imageUrl))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @ParameterizedTest
    @ValueSource(strings = {"사과!", "가나다라마바사아자차카타파하가나"})
    void 이름_규칙을_어기면_예외(String name) {
        assertThatThrownBy(() -> new Product(name, PRICE, IMAGE_URL))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 상품_정보를_수정한다() {
        Product product = new Product(NAME, PRICE, IMAGE_URL);

        product.update("콜라(500ml)", 2000, "http://example.com/cola.png");

        assertThat(product.getName()).isEqualTo("콜라(500ml)");
        assertThat(product.getPrice()).isEqualTo(2000);
        assertThat(product.getImageUrl()).isEqualTo("http://example.com/cola.png");
    }

    @Test
    void 잘못된_값으로_수정하면_예외가_나고_기존_값은_유지된다() {
        Product product = new Product(NAME, PRICE, IMAGE_URL);

        assertThatThrownBy(() -> product.update("콜라", -1, IMAGE_URL))
                .isInstanceOf(IllegalArgumentException.class);
        assertThat(product.getName()).isEqualTo(NAME);
        assertThat(product.getPrice()).isEqualTo(PRICE);
    }
}