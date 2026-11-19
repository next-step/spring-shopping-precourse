package shopping.product;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class ProductServiceTest {

    @Autowired
    private ProductService productService;

    @Test
    void 상품을_생성한다() {
        // given
        String name = "키보드";
        Integer price = 50000;
        String imageUrl = "http://img.com/a.png";

        // when
        Product product = productService.create(name, price, imageUrl);

        // then
        assertThat(product.getId()).isNotNull();
        assertThat(product.getName()).isEqualTo(name);
        assertThat(product.getPrice()).isEqualTo(price);
        assertThat(product.getImageUrl()).isEqualTo(imageUrl);
    }
}