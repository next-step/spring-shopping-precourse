package shopping.product.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;
import shopping.product.domain.Product;
import shopping.product.domain.ProductRepository;
import shopping.product.dto.ProductRequest;
import shopping.product.dto.ProductResponse;
import shopping.product.exception.ProfanityProductNameException;
import shopping.product.exception.ProductNotFoundException;

@SpringBootTest
@Transactional
class ProductServiceTest {

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;

    @MockitoBean
    private ProfanityChecker profanityChecker;

    @Test
    @DisplayName("상품을 생성하면 저장되고 생성된 상품을 반환한다")
    void create() {
        ProductRequest request =
                new ProductRequest("캐러멜 팝콘", 5_000L, "https://example.com/popcorn.jpg");

        ProductResponse response = productService.create(request);

        Product product = productRepository.findById(response.id()).orElseThrow();
        assertThat(product.getName()).isEqualTo("캐러멜 팝콘");
        assertThat(product.getPrice()).isEqualTo(5_000L);
        assertThat(product.getImageUrl()).isEqualTo("https://example.com/popcorn.jpg");
    }

    @Test
    @DisplayName("상품 ID로 특정 상품을 조회한다")
    void findById() {
        Product product = productRepository.save(
                new Product("캐러멜 팝콘", 5_000L, "https://example.com/popcorn.jpg"));

        ProductResponse response = productService.findById(product.getId());

        assertThat(response.id()).isEqualTo(product.getId());
        assertThat(response.name()).isEqualTo("캐러멜 팝콘");
        assertThat(response.price()).isEqualTo(5_000L);
        assertThat(response.imageUrl()).isEqualTo("https://example.com/popcorn.jpg");
    }

    @Test
    @DisplayName("존재하지 않는 상품을 조회하면 예외가 발생한다")
    void findByUnknownId() {
        assertThatThrownBy(() -> productService.findById(999L))
                .isInstanceOf(ProductNotFoundException.class)
                .hasMessage("존재하지 않는 상품입니다.");
    }

    @Test
    @DisplayName("모든 상품 목록을 조회한다")
    void findAll() {
        productRepository.saveAll(List.of(
                new Product("캐러멜 팝콘", 5_000L, "https://example.com/popcorn.jpg"),
                new Product("콜라", 2_000L, "https://example.com/cola.jpg")));

        List<ProductResponse> responses = productService.findAll();

        assertThat(responses).extracting(ProductResponse::name)
                .containsExactlyInAnyOrder("캐러멜 팝콘", "콜라");
    }

    @Test
    @DisplayName("상품이 없으면 빈 목록을 반환한다")
    void findAllWhenEmpty() {
        assertThat(productService.findAll()).isEmpty();
    }

    @Test
    @DisplayName("기존 상품의 정보를 수정한다")
    void update() {
        Product product = productRepository.save(
                new Product("캐러멜 팝콘", 5_000L, "https://example.com/popcorn.jpg"));
        ProductRequest request =
                new ProductRequest("콜라", 2_000L, "https://example.com/cola.jpg");

        ProductResponse response = productService.update(product.getId(), request);

        assertThat(response.name()).isEqualTo("콜라");
        assertThat(response.price()).isEqualTo(2_000L);
        assertThat(response.imageUrl()).isEqualTo("https://example.com/cola.jpg");
    }

    @Test
    @DisplayName("존재하지 않는 상품을 수정하면 예외가 발생한다")
    void updateUnknownProduct() {
        ProductRequest request =
                new ProductRequest("콜라", 2_000L, "https://example.com/cola.jpg");

        assertThatThrownBy(() -> productService.update(999L, request))
                .isInstanceOf(ProductNotFoundException.class)
                .hasMessage("존재하지 않는 상품입니다.");
    }

    @Test
    @DisplayName("특정 상품을 삭제한다")
    void delete() {
        Product product = productRepository.save(
                new Product("캐러멜 팝콘", 5_000L, "https://example.com/popcorn.jpg"));

        productService.delete(product.getId());

        assertThat(productRepository.existsById(product.getId())).isFalse();
    }

    @Test
    @DisplayName("존재하지 않는 상품을 삭제하면 예외가 발생한다")
    void deleteUnknownProduct() {
        assertThatThrownBy(() -> productService.delete(999L))
                .isInstanceOf(ProductNotFoundException.class)
                .hasMessage("존재하지 않는 상품입니다.");
    }

    @Test
    @DisplayName("비속어가 포함된 이름으로 상품을 생성하면 예외가 발생한다")
    void createWithProfanity() {
        ProductRequest request =
                new ProductRequest("badword", 5_000L, "https://example.com/product.jpg");
        when(profanityChecker.containsProfanity("badword")).thenReturn(true);

        assertThatThrownBy(() -> productService.create(request))
                .isInstanceOf(ProfanityProductNameException.class)
                .hasMessage("상품 이름에 비속어가 포함되어 있습니다.");
        assertThat(productRepository.count()).isZero();
    }

    @Test
    @DisplayName("비속어가 포함된 이름으로 상품을 수정하면 예외가 발생한다")
    void updateWithProfanity() {
        Product product = productRepository.save(
                new Product("상품", 5_000L, "https://example.com/product.jpg"));
        ProductRequest request =
                new ProductRequest("badword", 2_000L, "https://example.com/changed.jpg");
        when(profanityChecker.containsProfanity("badword")).thenReturn(true);

        assertThatThrownBy(() -> productService.update(product.getId(), request))
                .isInstanceOf(ProfanityProductNameException.class);
        assertThat(product.getName()).isEqualTo("상품");
    }
}
