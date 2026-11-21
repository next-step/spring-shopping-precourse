package shopping.repository;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import shopping.model.Product;
import shopping.model.ProductName;

class ProductRepositoryTest {
    private ProductRepository productRepository;

    @BeforeEach
    void setUp() {
        productRepository = new ProductRepository();
    }

    @Test
    @DisplayName("상품을 저장하고 조회한 뒤 삭제할 수 있다")
    void saveFindAndDeleteProduct() {
        Product product = productRepository.save(
                new Product(new ProductName("상품"), 1_000L, "image-url"));

        assertThat(productRepository.findById(product.getId())).contains(product);
        productRepository.delete(product);
        assertThat(productRepository.findById(product.getId())).isEmpty();
    }

    @Test
    @DisplayName("상품이 없으면 빈 목록을 반환한다")
    void returnEmptyListWhenNoProducts() {
        assertThat(productRepository.findAll()).isEmpty();
    }
}
