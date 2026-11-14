package shopping.api.entity;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import shopping.domain.entity.ImageUrl;
import shopping.domain.entity.Name;
import shopping.domain.entity.Price;
import shopping.domain.entity.Product;

import static org.assertj.core.api.Assertions.*;


public class ProductTest {

    @Test
    @DisplayName("상품은 이름, 가격, 이미지를 가지고 있어야 한다.")
    void constructorTest() {
        Product product = new Product(new Name("책"), new Price(2000), new ImageUrl("https://image.png"));

        assertThat(product.getName().getValue()).isEqualTo(new Name("책").getValue());
        assertThat(product.getPrice().getValue()).isEqualTo(new Price(2000).getValue());
        assertThat(product.getImageUrl().getValue()).isEqualTo(new ImageUrl("https://image.png").getValue());

    }


}
