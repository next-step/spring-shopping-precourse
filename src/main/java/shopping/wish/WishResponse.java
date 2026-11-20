package shopping.wish;

import shopping.product.Product;
import shopping.product.ProductResponse;

public record WishResponse(Long id, ProductResponse product) {
    public static WishResponse of(Wish wish, Product product) {
        return new WishResponse(wish.getId(), ProductResponse.from(product));
    }
}