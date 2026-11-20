package shopping.controller;

import shopping.domain.Product;

public record ProductResponse(long id, String name, long price, String imageUrl) {
    public static ProductResponse from(Product product) {
        return new ProductResponse(
                product.id().orElseThrow().value(),
                product.name().value(),
                product.price().value(),
                product.image().url());
    }
}
