package shopping.product.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import shopping.product.domain.Product;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@DisplayName("상품 저장소")
class ProductRepositoryTest {

    @Autowired
    private ProductRepository products;
    @Autowired
    private EntityManager entityManager;

    @DisplayName("상품 ID를 부여하고 저장한 상품 정보를 복원한다")
    @Test
    void assignsIdAndRestoresAllProductDetails() {
        Product saved = products.saveAndFlush(
                new Product("상품", 1000, "https://example.com/a"));
        entityManager.clear();

        Product found = products.findById(saved.id()).orElseThrow();

        assertThat(found.id()).isPositive();
        assertThat(found.name()).isEqualTo("상품");
        assertThat(found.price()).isEqualTo(1000);
        assertThat(found.imageUrl()).isEqualTo("https://example.com/a");
    }
}
