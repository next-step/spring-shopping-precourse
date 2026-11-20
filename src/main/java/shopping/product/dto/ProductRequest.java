package shopping.product.dto;

import jakarta.validation.constraints.NotNull;
import shopping.product.ImageUrl;
import shopping.product.Price;
import shopping.product.Product;
import shopping.product.ProductName;

/**
 * price를 Integer로 받으므로 요청에 price가 빠지면 null이 된다.
 * null을 Price(int)에 넘기면 NullPointerException(500)이 나므로 @NotNull로 먼저 막는다(400).
 */
public record ProductRequest(
        String name,
        @NotNull(message = "상품 가격은 필수입니다.") Integer price,
        String imageUrl) {

    public Product toEntity() {
        return new Product(new ProductName(name), new Price(price), new ImageUrl(imageUrl));
    }
}
