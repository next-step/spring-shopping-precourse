package shopping.product.dto;

import shopping.product.entity.Product;

public record ProductResponse(
        Long id, String name, Long price, String url
) {
    public static ProductResponse from(Product product) {
        return new ProductResponse(product.getId(), product.getName(),
                product.getPrice(), product.getUrl());
    }
}
