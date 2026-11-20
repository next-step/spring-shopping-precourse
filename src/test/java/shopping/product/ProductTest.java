package shopping.product;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

class ProductTest {
    private static final String IMAGE_URL = "https://example.com/cola.jpg";
    private static final String NEW_IMAGE_URL = "https://example.com/cider.jpg";

    @Test 
    void 상품을_생성하면_입력한_값을_가진다() {
        Product product = new Product(new ProductName("콜라"), new Price(2000), new ImageUrl(IMAGE_URL));

        assertThat(product.getName()).isEqualTo("콜라");
        assertThat(product.getPrice()).isEqualTo(2000);
        assertThat(product.getImageUrl()).isEqualTo(IMAGE_URL);
    } 

    @Test
    void 상품을_수정하면_입력한_값으로_변경된다() {
        Product product = new Product(new ProductName("콜라"), new Price(2000), new ImageUrl(IMAGE_URL));

        product.update(new ProductName("사이다"), new Price(1900), new ImageUrl(NEW_IMAGE_URL));

        assertThat(product.getName()).isEqualTo("사이다");
        assertThat(product.getPrice()).isEqualTo(1900);
        assertThat(product.getImageUrl()).isEqualTo(NEW_IMAGE_URL);
    }

    @Test
    void 잘못된_이름으로_상품을_생성하면_예외가_발생한다() {
        assertThatThrownBy(() -> new Product(new ProductName("콜라!"), new Price(2000), new ImageUrl(IMAGE_URL)))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 잘못된_값으로_상품을_수정하면_예외가_발생하고_기존_값이_유지된다() {
        Product product = new Product(new ProductName("콜라"), new Price(2000), new ImageUrl(IMAGE_URL));
        assertThatThrownBy(() -> product.update(new ProductName("사이다!"), new Price(-2000), new ImageUrl(NEW_IMAGE_URL)))
                .isInstanceOf(IllegalArgumentException.class);
        assertThat(product.getName()).isEqualTo("콜라");
        assertThat(product.getPrice()).isEqualTo(2000);
        assertThat(product.getImageUrl()).isEqualTo(IMAGE_URL);
    }
}
