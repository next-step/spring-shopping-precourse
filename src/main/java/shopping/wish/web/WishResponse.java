package shopping.wish.web;

import shopping.product.web.ProductResponse;
import shopping.wish.domain.Wish;

public record WishResponse(Long id, ProductResponse product) {

    public static WishResponse of(Wish wish) {
        return new WishResponse(wish.id(), ProductResponse.of(wish.product()));
    }
}
