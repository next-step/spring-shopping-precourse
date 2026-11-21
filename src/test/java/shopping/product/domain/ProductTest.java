package shopping.product.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ProductTest {

    @Test
    @DisplayName("상품은 이름, 가격, 이미지 URL로 구성된다")
    void createProduct() {
        Product product = new Product("캐러멜 팝콘", 5_000L, "https://example.com/popcorn.jpg");

        assertThat(product.getName()).isEqualTo("캐러멜 팝콘");
        assertThat(product.getPrice()).isEqualTo(5_000L);
        assertThat(product.getImageUrl()).isEqualTo("https://example.com/popcorn.jpg");
    }

    @Test
    @DisplayName("상품의 이름, 가격, 이미지 URL을 수정한다")
    void update() {
        Product product = new Product("캐러멜 팝콘", 5_000L, "https://example.com/popcorn.jpg");

        product.update("콜라", 2_000L, "https://example.com/cola.jpg");

        assertThat(product.getName()).isEqualTo("콜라");
        assertThat(product.getPrice()).isEqualTo(2_000L);
        assertThat(product.getImageUrl()).isEqualTo("https://example.com/cola.jpg");
    }
}
