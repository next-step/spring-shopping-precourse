package shopping;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

import jakarta.persistence.EntityManager;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@DataJpaTest(
        properties = {
            "spring.flyway.enabled=false",
            "spring.datasource.password=",
            "spring.jpa.hibernate.ddl-auto=create-drop"
        })
@Import(ProductService.class)
class ProductServiceTest {
    @MockitoBean private ProfanityChecker profanityChecker;

    @Autowired private ProductService productService;

    @Autowired private ProductRepository productRepository;

    @Autowired private EntityManager entityManager;

    @Test
    void createSaveProduct() {
        Product saved = productService.create("키보드", 50000, "https://example.com/keyboard.jpg");

        entityManager.flush();
        entityManager.clear();

        Product found = productRepository.findById(saved.getId()).orElseThrow();

        assertThat(found.getId()).isNotNull();
        assertThat(found.getName()).isEqualTo("키보드");
        assertThat(found.getPrice()).isEqualTo(50000);
        assertThat(found.getImageUrl()).isEqualTo("https://example.com/keyboard.jpg");
    }

    @Test
    void findByIdReturnSavedPruduct() {
        Product saved = productService.create("키보드", 50000, "https://example.com/keyboard.jpg");
        entityManager.flush();
        entityManager.clear();

        Product found = productService.findById(saved.getId());

        assertThat(found.getId()).isEqualTo(saved.getId());
        assertThat(found.getName()).isEqualTo("키보드");
        assertThat(found.getPrice()).isEqualTo(50000);
        assertThat(found.getImageUrl()).isEqualTo("https://example.com/keyboard.jpg");
    }

    @Test
    void findByIdThrowWhenProductDoesNotExist() {
        assertThatThrownBy(() -> productService.findById(-1L))
                .isInstanceOf(ProductNotFoundException.class);
    }

    @Test
    void findAllReturnsSavedProducts() {
        Product first = productService.create("키보드", 50000, "https://example.com/keyboard.jpg");
        Product second = productService.create("마우스", 40000, "https://example.com/mouse.jpg");

        entityManager.flush();
        entityManager.clear();

        List<Product> products = productService.findAll();

        assertThat(products)
                .extracting(Product::getId)
                .containsExactlyInAnyOrder(first.getId(), second.getId());
    }

    @Test
    void findAllReturnsEmptyListWhenNoProductsExist() {
        assertThat(productService.findAll()).isEmpty();
    }

    @Test
    void updateChangesSavedProduct() {
        Product saved = productService.create("키보드", 50000, "https://example.com/keyboard.jpg");
        productService.update(saved.getId(), "마우스", 40000, "https://example.com/mouse.jpg");
        entityManager.flush();
        entityManager.clear();

        Product found = productRepository.findById(saved.getId()).orElseThrow();

        assertThat(found.getName()).isEqualTo("마우스");
        assertThat(found.getPrice()).isEqualTo(40000);
        assertThat(found.getImageUrl()).isEqualTo("https://example.com/mouse.jpg");
    }

    @Test
    void updateThrowsWhenProductDoesNotExist() {
        assertThatThrownBy(
                        () ->
                                productService.update(
                                        -1L, "마우스", 20000, "https://example.com/moudse.jpg"))
                .isInstanceOf(ProductNotFoundException.class);
    }

    @Test
    void deleteRemovesSavedProduct() {
        Product saved = productService.create("키보드", 50000, "https://example.com/keyboard.jpg");

        productService.delete(saved.getId());
        entityManager.flush();
        entityManager.clear();

        assertThat(productRepository.findById(saved.getId())).isEmpty();
    }

    @Test
    void deleteThrowsWhenProductDoesNotExist() {
        assertThatThrownBy(() -> productService.delete(-1L))
                .isInstanceOf(ProductNotFoundException.class);
    }

    @Test
    void createRejectsProfanityWithoutSaving() {
        given(profanityChecker.containsProfanity("badword")).willReturn(true);

        assertThatThrownBy(
                        () -> productService.create("badword", 1000, "https://example.com/a.jpg"))
                .isInstanceOf(ProfanityDetectedException.class);

        assertThat(productRepository.findAll()).isEmpty();
    }

    @Test
    void updateRejectsProfanityWithoutChangingProduct() {
        Product saved = productService.create("키보드", 50000, "https://example.com/keyboard.jpg");
        given(profanityChecker.containsProfanity("badword")).willReturn(true);
        assertThatThrownBy(
                        () ->
                                productService.update(
                                        saved.getId(),
                                        "badword",
                                        1000,
                                        "https://example.com/a.jpg"))
                .isInstanceOf(ProfanityDetectedException.class);
        assertOriginalProductPersisted(saved.getId());
    }

    @Test
    void createDoesNotSaveWhenProfanityServiceFails() {
        given(profanityChecker.containsProfanity("키보드"))
                .willThrow(new ProfanityServiceUnavailableException());

        assertThatThrownBy(
                        () ->
                                productService.create(
                                        "키보드", 50000, "https://example.com/keyboard.jpg"))
                .isInstanceOf(ProfanityServiceUnavailableException.class);

        assertThat(productRepository.findAll()).isEmpty();
    }

    @Test
    void updateKeepsProductWhenProfanityServiceFails() {
        Product saved = productService.create("키보드", 50000, "https://example.com/keyboard.jpg");
        given(profanityChecker.containsProfanity("마우스"))
                .willThrow(new ProfanityServiceUnavailableException());
        assertThatThrownBy(
                        () ->
                                productService.update(
                                        saved.getId(),
                                        "마우스",
                                        20000,
                                        "https://example.com/mouse.jpg"))
                .isInstanceOf(ProfanityServiceUnavailableException.class);
        assertOriginalProductPersisted(saved.getId());
    }

    private void assertOriginalProductPersisted(Long productId) {
        entityManager.flush();
        entityManager.clear();
        Product found = productRepository.findById(productId).orElseThrow();
        assertThat(found.getName()).isEqualTo("키보드");
        assertThat(found.getPrice()).isEqualTo(50000);
        assertThat(found.getImageUrl()).isEqualTo("https://example.com/keyboard.jpg");
    }
}
