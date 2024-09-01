package shopping.api.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import shopping.api.entity.CreateProductRequest;
import shopping.domain.entity.ImageUrl;
import shopping.domain.entity.Name;
import shopping.domain.entity.Price;
import shopping.domain.entity.Product;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
public class ProductServiceTest {

    @Autowired
    private ProductService productService;

    @Test
    @DisplayName("상품을 조회한다.")
    void findProductTest() {
        Product product = 상품_생성("책",2000,"http://image.png");

        Product actual = productService.findById(product.getId());

        assertThat(actual.getName().getValue()).isEqualTo(product.getName().getValue());
        assertThat(actual.getPrice().getValue()).isEqualTo(product.getPrice().getValue());
        assertThat(actual.getImageUrl().getValue()).isEqualTo(product.getImageUrl().getValue());
    }


    Product 상품_생성(String name, int price, String imageUrl) {
        CreateProductRequest createProductRequest = new CreateProductRequest(new Name(name), new Price(price), new ImageUrl(imageUrl));


        Product savedProduct = productService.save(createProductRequest);
        
        assertThat(savedProduct.getName().getValue()).isEqualTo(name);
        assertThat(savedProduct.getPrice().getValue()).isEqualTo(price);
        assertThat(savedProduct.getImageUrl().getValue()).isEqualTo(imageUrl);

        return savedProduct;
    }
}
