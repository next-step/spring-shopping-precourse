package shopping.product.web;

import shopping.product.domain.Product;

public record ProductResponse(Long id, String name, Integer price, String imageUrl) {

    public static ProductResponse of(Product product) {
        return new ProductResponse(product.id(), product.name(), product.price(),
                product.imageUrl());
    }
}
