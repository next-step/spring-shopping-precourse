package shopping.product;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import shopping.common.NotFoundException;
import shopping.product.dto.ProductRequest;
import shopping.product.dto.ProductResponse;

@SpringBootTest
@Transactional
class ProductServiceTest {

    private static final ProductRequest STRAWBERRY =
            new ProductRequest("딸기", 1000, "https://example.com/strawberry.jpg");
    private static final ProductRequest APPLE =
            new ProductRequest("사과", 2000, "https://example.com/apple.jpg");
    private static final ProductRequest BAD_WORD =
            new ProductRequest("fuck", 1000, "https://example.com/bad.jpg");

    @Autowired
    private ProductService productService;

    @Test
    @DisplayName("상품을 등록하면 번호가 생긴다")
    void createsProduct() {
        ProductResponse response = productService.create(STRAWBERRY);
        assertThat(response.id()).isNotNull();
        assertThat(response.name()).isEqualTo("딸기");
    }

    @Test
    @DisplayName("이름에 비속어가 있으면 예외가 발생한다")
    void throwsWhenProfanity() {
        assertThatThrownBy(() -> productService.create(BAD_WORD))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("없는 상품을 조회하면 예외가 발생한다")
    void throwsWhenNotFound() {
        assertThatThrownBy(() -> productService.findById(999L))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    @DisplayName("없는 상품을 수정하면 예외가 발생한다")
    void throwsWhenUpdateNotFound() {
        assertThatThrownBy(() -> productService.update(999L, APPLE))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    @DisplayName("없는 상품을 삭제하면 예외가 발생한다")
    void throwsWhenDeleteNotFound() {
        assertThatThrownBy(() -> productService.delete(999L))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    @DisplayName("상품 정보를 수정할 수 있다")
    void updatesProduct() {
        ProductResponse saved = productService.create(STRAWBERRY);
        ProductResponse updated = productService.update(saved.id(), APPLE);
        assertThat(updated.name()).isEqualTo("사과");
        assertThat(updated.price()).isEqualTo(2000);
    }

    @Test
    @DisplayName("삭제한 상품은 조회할 수 없다")
    void deletesProduct() {
        ProductResponse saved = productService.create(STRAWBERRY);
        productService.delete(saved.id());
        assertThatThrownBy(() -> productService.findById(saved.id()))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    @DisplayName("등록한 상품이 목록에 나온다")
    void findsAllProducts() {
        productService.create(STRAWBERRY);
        productService.create(APPLE);
        assertThat(productService.findAll()).hasSize(2);
    }
}