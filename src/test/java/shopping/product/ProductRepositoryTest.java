package shopping.product;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class ProductRepositoryTest {

    private static final String IMAGE = "https://example.com/a.png";

    private final ProductRepository repository = new ProductRepository();

    private Product newProduct(String name) {
        return new Product(null, new ProductName(name), 1000, IMAGE);
    }

    @Test
    void 저장하면_id가_부여된다() {
        Product saved = repository.save(newProduct("사과"));

        assertThat(saved.getId()).isNotNull();
    }

    @Test
    void id로_상품을_조회한다() {
        Product saved = repository.save(newProduct("사과"));

        assertThat(repository.findById(saved.getId())).isPresent();
    }

    @Test
    void 없는_id를_조회하면_비어_있다() {
        assertThat(repository.findById(999L)).isEmpty();
    }

    @Test
    void 전체_목록을_id_순서로_조회한다() {
        repository.save(newProduct("사과"));
        repository.save(newProduct("배"));

        assertThat(repository.findAll())
                .extracting(product -> product.getName().getValue())
                .containsExactly("사과", "배");
    }

    @Test
    void 삭제하면_조회되지_않는다() {
        Product saved = repository.save(newProduct("사과"));

        repository.deleteById(saved.getId());

        assertThat(repository.findById(saved.getId())).isEmpty();
    }
}