package shopping.wish.dto;

import shopping.product.dto.ProductResponse;
import shopping.wish.Wish;

public record WishResponse(Long id, ProductResponse product) {

    public static WishResponse from(Wish wish) {
        return new WishResponse(
                wish.getId(),
                ProductResponse.from(wish.getProduct())
        );
    }
}
