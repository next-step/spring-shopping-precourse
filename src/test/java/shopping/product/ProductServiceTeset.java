package shopping.product;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.transaction.annotation.Transactional;
import shopping.exception.NotFoundException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;


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
    @Test
    void 상품을_조회하다(){
        Product saved = productService.create("키보드", 50000, "http://img.com/a.png");
        Product found = productService.findById(saved.getId());
        assertThat(found.getName()).isEqualTo("키보드");
    }
    @Test
    void  존재하지_않는_상품을_조회하면_예외가_발생한다(){
        assertThatThrownBy(() -> productService.findById(999L))
                .isInstanceOf(NotFoundException.class);
    }
    @Test
    void 상품_목록을_조회한다(){
        productService.create("키보드", 50000, "http://img.com/a.png");
        productService.create("마우스", 3000, "http://img.com/b.png");

        List<Product> products = productService.findAll();
        assertThat(products).hasSize(2);
    }
    @Test
    void 상품을_수정한다(){
        Product saved = productService.create("키보드", 50000, "http://img.com/a.png");
        productService.update(saved.getId(), "마우스", 30000, "http://img.com/b.png");

        Product found = productService.findById(saved.getId());
        assertThat(found.getName()).isEqualTo("마우스");
        assertThat(found.getPrice()).isEqualTo(30000);
    }
    @Test
    void 상품을_삭제한다(){
        Product saved = productService.create("키보드", 50000, "http://img.com/a.png");
        productService.delete(saved.getId());
        assertThatThrownBy(() -> productService.findById(saved.getId()))
                .isInstanceOf(NotFoundException.class);
    }
}