package shopping.wish.dto;

import shopping.product.domain.Product;
import shopping.wish.domain.Wish;

public record WishResponse(
        Long id,
        Long productId,
        String name,
        Long price,
        String imageUrl
) {

    public static WishResponse from(Wish wish) {
        Product product = wish.getProduct();
        return new WishResponse(wish.getId(), product.getId(), product.getName(),
                product.getPrice(), product.getImageUrl());
    }
}
