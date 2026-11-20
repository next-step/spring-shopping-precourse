package shopping.wish.controller;

import shopping.product.controller.ProductResponse;
import shopping.wish.domain.Wish;

public record WishResponse(Long id, ProductResponse product) {
    public static WishResponse from(Wish wish) {
        return new WishResponse(wish.getId(), ProductResponse.from(wish.getProduct()));
    }
}