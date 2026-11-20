package shopping.product.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import shopping.product.domain.Product;
import shopping.product.domain.ProductNameValidator;
import shopping.product.domain.ProfanityChecker;
import shopping.product.exception.InvalidProductNameException;
import shopping.product.exception.ProductNotFoundException;

@DataJpaTest
@Import({ProductService.class, ProductNameValidator.class})
class ProductServiceTest {
    private static final String IMAGE_URL = "https://image.com/product.png";

    @Autowired
    private ProductService service;

    @Test
    void 상품을_등록하면_id가_붙은_상품이_들어온다() {
        Product created = service.create("머그컵", 5000, IMAGE_URL);

        assertThat(created.getId()).isNotNull();
        assertThat(created.getName()).isEqualTo("머그컵");
        assertThat(created.getPrice()).isEqualTo(5000);
    }

    @Test
    void 전체_조회하면_등록한_상품들이_모두_나온다() {
        Product firstProduct = service.create("컵", 3000, IMAGE_URL);
        Product secondProduct = service.create("머그컵", 5000, IMAGE_URL);

        List<Product> products = service.findAll();

        assertThat(products).hasSize(2);
        assertThat(products).contains(firstProduct, secondProduct);
    }

    @Test
    void id로_조회하면_그_상품이_나온다() {
        service.create("컵", 3000, IMAGE_URL);
        Product second = service.create("머그컵", 5000, IMAGE_URL);

        Product foundProduct = service.findById(second.getId());

        assertThat(foundProduct.getId()).isEqualTo(second.getId());
        assertThat(foundProduct.getName()).isEqualTo("머그컵");
    }

    @Test
    void 없는_id로_조회하면_예외가_난다() {
        assertThatThrownBy(() -> service.findById(999L))
                .isInstanceOf(ProductNotFoundException.class);
    }

    @Test
    void 수정하면_이름과_가격이_바뀐다() {
        Product product = service.create("컵", 3000, IMAGE_URL);

        service.update(product.getId(), "머그컵", 5000, IMAGE_URL);

        Product foundProduct = service.findById(product.getId());
        assertThat(foundProduct.getName()).isEqualTo("머그컵");
        assertThat(foundProduct.getPrice()).isEqualTo(5000);
        assertThat(service.findAll()).hasSize(1);
    }

    @Test
    void 없는_id를_수정하면_예외가_난다() {
        assertThatThrownBy(() -> service.update(999L, "머그컵", 5000, IMAGE_URL))
                .isInstanceOf(ProductNotFoundException.class);
    }

    @Test
    void 삭제하면_그_상품은_더_이상_조회되지_않는다() {
        Product firstProduct = service.create("컵", 3000, IMAGE_URL);
        Product secondProduct = service.create("머그컵", 5000, IMAGE_URL);

        service.delete(secondProduct.getId());

        assertThat(service.findById(firstProduct.getId())).isEqualTo(firstProduct);
        assertThatThrownBy(() -> service.findById(secondProduct.getId()))
                .isInstanceOf(ProductNotFoundException.class);
        assertThat(service.findAll()).hasSize(1);
    }

    @Test
    void 없는_id를_삭제하면_예외가_난다() {
        assertThatThrownBy(() -> service.delete(999L))
                .isInstanceOf(ProductNotFoundException.class);
    }

    @Test
    void 규칙에_어긋난_이름으로_등록하면_예외가_나고_저장되지_않는다() {
        assertThatThrownBy(() -> service.create("a".repeat(16), 3000, IMAGE_URL))
                .isInstanceOf(InvalidProductNameException.class);
        assertThat(service.findAll()).hasSize(0);
    }

    @Test
    void 규칙에_어긋난_이름으로_수정하면_예외가_나고_기존_상품은_그대로다() {
        Product product = service.create("컵", 3000, IMAGE_URL);

        assertThatThrownBy(() -> service.update(product.getId(), "a".repeat(16), 3000, IMAGE_URL))
                .isInstanceOf(InvalidProductNameException.class);
        assertThat(service.findById(product.getId()).getName()).isEqualTo("컵");
    }

    @Test
    void 비속어가_든_이름으로_등록하면_예외가_나고_저장되지_않는다() {
        assertThatThrownBy(() -> service.create("비속어컵", 3000, IMAGE_URL))
                .isInstanceOf(InvalidProductNameException.class);
        assertThat(service.findAll()).hasSize(0);
    }

    @Test
    void 비속어가_든_이름으로_수정하면_예외가_나고_기존_상품은_그대로다() {
        Product product = service.create("컵", 3000, IMAGE_URL);
        assertThatThrownBy(() -> service.update(product.getId(), "비속어컵", 3000, IMAGE_URL))
                .isInstanceOf(InvalidProductNameException.class);
        assertThat(service.findById(product.getId()).getName()).isEqualTo("컵");
    }

    @TestConfiguration
    static class FakeProfanityConfig {
        @Bean
        ProfanityChecker profanityChecker() {
            return text -> text.contains("비속어");
        }
    }
}
