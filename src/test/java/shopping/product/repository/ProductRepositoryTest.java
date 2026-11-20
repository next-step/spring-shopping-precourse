package shopping.product.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import shopping.product.domain.Product;

@DataJpaTest
class ProductRepositoryTest {
    private static final String IMAGE_URL = "https://image.com/product.png";

    @Autowired
    private ProductRepository repository;

    @Test
    void 저장한_상품이_없으면_목록이_비어있다() {
        List<Product> products = repository.findAll();

        assertThat(products).isEmpty();
    }

    @Test
    void 저장하면_id가_붙은_상품이_돌아온다() {
        Product product = createProduct("컵", 3000);

        Product saved = repository.save(product);

        assertThat(saved.getId()).isNotNull();
    }

    @Test
    void 저장한_상품을_id로_찾으면_저장할_때의_이름가격이미지URL이_그대로_나온다() {
        Product product = createProduct("컵", 3000);

        Product saved = repository.save(product);
        Optional<Product> findResult = repository.findById(saved.getId());

        assertThat(findResult).isPresent();
        Product found = findResult.get();

        assertThat(found.getName()).isEqualTo("컵");
        assertThat(found.getPrice()).isEqualTo(3000);
        assertThat(found.getImageUrl()).isEqualTo(IMAGE_URL);
    }

    @Test
    void 저장소에_없는_id로_찾으면_빈_결과가_나온다() {
        repository.save(createProduct("컵", 3000));

        Optional<Product> findResult = repository.findById(999L);
        assertThat(findResult).isNotPresent();
    }

    @Test
    void 상품_두_개를_저장하면_서로_다른_id가_붙는다() {
        Product firstProduct = repository.save(createProduct("컵", 3000));
        Product secondProduct = repository.save(createProduct("자동차", 10000));

        assertThat(firstProduct.getId()).isNotEqualTo(secondProduct.getId());
    }

    @Test
    void 상품_두_개를_저장하면_목록에_둘_다_나온다() {
        Product firstProduct = repository.save(createProduct("컵", 3000));
        Product secondProduct = repository.save(createProduct("자동차", 10000));

        List<Product> productList = repository.findAll();

        assertThat(productList).hasSize(2);
        assertThat(productList).contains(firstProduct, secondProduct);
    }

    @Test
    void 같은_id로_다시_저장하면_덮어쓴다() {
        Product savedProduct = repository.save(createProduct("컵", 3000));
        Product updatedProduct = new Product(savedProduct.getId(), "자동차", 10000, IMAGE_URL);
        repository.save(updatedProduct);

        Optional<Product> findResult = repository.findById(savedProduct.getId());

        assertThat(findResult).isPresent();
        Product found = findResult.get();

        assertThat(found.getName()).isEqualTo("자동차");
        assertThat(repository.findAll()).hasSize(1);
    }

    @Test
    void 삭제하면_그_id로_더_이상_찾을_수_없다() {
        Product firstProduct = repository.save(createProduct("컵", 3000));
        Product secondProduct = repository.save(createProduct("자동차", 10000));

        repository.deleteById(secondProduct.getId());

        Optional<Product> firstFoundResult = repository.findById(firstProduct.getId());
        Optional<Product> secondFoundResult = repository.findById(secondProduct.getId());

        assertThat(firstFoundResult).isPresent();
        assertThat(secondFoundResult).isNotPresent();
    }

    private Product createProduct(String name, int price) {
        return new Product(null, name, price, IMAGE_URL);
    }
}
