package shopping.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import shopping.entity.Product;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ProductRepositoryTests {

    private static final String IMAGE_URL = "https://api.dicebear.com/10.x/waves/svg";

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private TestEntityManager entityManager;

    @DisplayName("상품을 저장하면 id가 생성된다")
    @Test
    void save() {
        Product product = Product.of("치킨", 20000, IMAGE_URL);

        Product saved = productRepository.save(product);

        assertThat(saved.getId()).isNotNull();
    }

    @DisplayName("id로 상품을 조회한다")
    @Test
    void findById() {
        Product saved = productRepository.save(Product.of("치킨", 20000, IMAGE_URL));

        Optional<Product> found = productRepository.findById(saved.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("치킨");
    }

    @DisplayName("존재하지 않는 id로 조회하면 빈 값을 반환한다")
    @Test
    void findByIdNotFound() {
        Optional<Product> found = productRepository.findById(999L);

        assertThat(found).isEmpty();
    }

    @DisplayName("전체 상품을 조회한다")
    @Test
    void findAll() {
        productRepository.save(Product.of("치킨", 20000, IMAGE_URL));
        productRepository.save(Product.of("피자", 25000, IMAGE_URL));

        List<Product> products = productRepository.findAll();

        assertThat(products).hasSize(2);
    }

    @DisplayName("상품을 수정하면 id는 유지되고 나머지 필드가 변경되어 저장된다")
    @Test
    void update() {
        Product saved = productRepository.save(Product.of("치킨", 20000, IMAGE_URL));
        productRepository.flush();
        entityManager.clear();

        Product found = productRepository.findById(saved.getId()).orElseThrow();
        found.update("피자", 25000, "https://example.com/b.svg");
        productRepository.flush();
        entityManager.clear();

        Product updated = productRepository.findById(saved.getId()).orElseThrow();
        assertThat(updated.getId()).isEqualTo(saved.getId());
        assertThat(updated.getName()).isEqualTo("피자");
        assertThat(updated.getPrice()).isEqualTo(25000);
        assertThat(updated.getImageUrl()).isEqualTo("https://example.com/b.svg");
    }

    @DisplayName("id로 상품을 삭제한다")
    @Test
    void deleteById() {
        Product saved = productRepository.save(Product.of("치킨", 20000, IMAGE_URL));

        productRepository.deleteById(saved.getId());

        assertThat(productRepository.findById(saved.getId())).isEmpty();
    }
}
