package shopping.wish;

import shopping.product.Product;

public record WishResponse(Long id, Long productId, String name, int price, String imageUrl) {

    public static WishResponse from(Wish wish) {
        Product product = wish.getProduct();
        return new WishResponse(wish.getId(), product.getId(), product.getName(),
                product.getPrice(), product.getImageUrl());
    }
}
