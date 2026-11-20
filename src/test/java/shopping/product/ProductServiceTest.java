package shopping.product;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

class ProductServiceTest {

    private static final String IMAGE = "https://example.com/a.png";

    private final ProductService service = new ProductService(
            new ProductRepository(), new ProductNameFactory(text -> false));

    private Product createProduct(String name) {
        return service.create(name, 1000, IMAGE);
    }

    @Test
    void 상품을_생성하면_id가_부여된다() {
        assertThat(createProduct("사과").getId()).isNotNull();
    }

    @Test
    void 생성한_상품을_조회한다() {
        Product created = createProduct("사과");

        assertThat(service.get(created.getId()).getName().getValue()).isEqualTo("사과");
    }

    @Test
    void 없는_상품을_조회하면_예외가_발생한다() {
        assertThatThrownBy(() -> service.get(999L))
                .isInstanceOf(ProductNotFoundException.class);
    }

    @Test
    void 전체_상품을_조회한다() {
        createProduct("사과");
        createProduct("배");

        assertThat(service.getAll()).hasSize(2);
    }

    @Test
    void 상품을_수정하면_값이_바뀌고_id는_유지된다() {
        Product created = createProduct("사과");

        Product updated = service.update(created.getId(), "배", 2000, IMAGE);

        assertThat(updated.getId()).isEqualTo(created.getId());
        assertThat(updated.getName().getValue()).isEqualTo("배");
        assertThat(updated.getPrice()).isEqualTo(2000);
    }

    @Test
    void 없는_상품을_수정하면_예외가_발생한다() {
        assertThatThrownBy(() -> service.update(999L, "배", 2000, IMAGE))
                .isInstanceOf(ProductNotFoundException.class);
    }

    @Test
    void 상품을_삭제하면_조회되지_않는다() {
        Product created = createProduct("사과");

        service.delete(created.getId());

        assertThatThrownBy(() -> service.get(created.getId()))
                .isInstanceOf(ProductNotFoundException.class);
    }

    @Test
    void 없는_상품을_삭제하면_예외가_발생한다() {
        assertThatThrownBy(() -> service.delete(999L))
                .isInstanceOf(ProductNotFoundException.class);
    }

    @Test
    void 비속어가_포함된_이름으로_생성하면_예외가_발생한다() {
        ProductService strictService = new ProductService(
                new ProductRepository(), new ProductNameFactory(text -> true));

        assertThatThrownBy(() -> strictService.create("사과", 1000, IMAGE))
                .isInstanceOf(IllegalArgumentException.class);
    }
}