package shopping.wish;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import shopping.product.Product;
import shopping.product.ProductName;
import shopping.product.ProductNotFoundException;
import shopping.product.ProductRepository;

class WishServiceTest {

    private static final String IMAGE = "https://example.com/a.png";

    private final ProductRepository productRepository = new ProductRepository();
    private final WishService service =
            new WishService(new WishRepository(), productRepository);

    private Long saveProduct(String name) {
        Product product = new Product(null, new ProductName(name), 1000, IMAGE);
        return productRepository.save(product).getId();
    }

    @Test
    void 위시리스트에_상품을_추가한다() {
        WishResponse response = service.add(1L, saveProduct("apple"));

        assertThat(response.id()).isNotNull();
        assertThat(response.product().name()).isEqualTo("apple");
    }

    @Test
    void 없는_상품을_추가하면_예외가_발생한다() {
        assertThatThrownBy(() -> service.add(1L, 999L))
                .isInstanceOf(ProductNotFoundException.class);
    }

    @Test
    void 상품_id가_없으면_예외가_발생한다() {
        assertThatThrownBy(() -> service.add(1L, null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 같은_상품을_두_번_추가하면_예외가_발생한다() {
        Long productId = saveProduct("apple");
        service.add(1L, productId);

        assertThatThrownBy(() -> service.add(1L, productId))
                .isInstanceOf(DuplicateWishException.class);
    }

    @Test
    void 다른_회원은_같은_상품을_담을_수_있다() {
        Long productId = saveProduct("apple");
        service.add(1L, productId);

        assertThat(service.add(2L, productId).id()).isNotNull();
    }

    @Test
    void 내_위시리스트만_조회한다() {
        service.add(1L, saveProduct("apple"));
        service.add(2L, saveProduct("pear"));

        assertThat(service.getAll(1L)).hasSize(1);
    }

    @Test
    void 삭제된_상품의_위시는_조회에서_제외된다() {
        Long productId = saveProduct("apple");
        service.add(1L, productId);
        productRepository.deleteById(productId);

        assertThat(service.getAll(1L)).isEmpty();
    }

    @Test
    void 위시를_삭제하면_조회되지_않는다() {
        WishResponse response = service.add(1L, saveProduct("apple"));

        service.remove(1L, response.id());

        assertThat(service.getAll(1L)).isEmpty();
    }

    @Test
    void 없는_위시를_삭제하면_예외가_발생한다() {
        assertThatThrownBy(() -> service.remove(1L, 999L))
                .isInstanceOf(WishNotFoundException.class);
    }

    @Test
    void 다른_회원의_위시를_삭제하면_예외가_발생한다() {
        WishResponse response = service.add(1L, saveProduct("apple"));

        assertThatThrownBy(() -> service.remove(2L, response.id()))
                .isInstanceOf(WishNotFoundException.class);
    }
}