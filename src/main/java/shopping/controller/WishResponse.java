package shopping.controller;

import shopping.domain.Product;

public record WishResponse(long wishId, ProductResponse product) {
    public static WishResponse from(Product product) {
        ProductResponse response = ProductResponse.from(product);
        return new WishResponse(response.id(), response);
    }
}
