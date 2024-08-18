package shopping.product;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import shopping.model.Product;

import static org.assertj.core.api.Assertions.*;

public class ProductTest {
    @Test
    void 상품은_이름을_가지고_있다() {
        //given
        String name = "name";
        //when
        Product product = new Product(name);

        //then
        assertThat(product.getName()).isEqualTo(name);
    }

    @Test
    void 상품은_가격을_가지고_있다() {
        //given
        String name = "name";
        Integer price = 1000;
        //when
        Product product = new Product(name, price);
        //then
        assertThat(product.getPrice()).isEqualTo(price);
    }

    @Test
    void 상품은_이미지url을_가지고_있다() {
        //given
        String name = "name";
        Integer price = 1000;
        String url = "url";
        //when
        Product product = new Product(name, price, url);
        //then
        assertThat(product.getImageUrl()).isEqualTo(url);
    }

    @Test
    void 상품이름은_15자_초과_예외처리() {
        //given
        String name = "namenamenamename";
        //when

        //then
        Assertions.assertThatThrownBy(() -> new Product(name))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 상품이름에_특수문자_예외처리() {
        //given
        String name = "name#";
        //when

        //then
        Assertions.assertThatThrownBy(() -> new Product(name))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
