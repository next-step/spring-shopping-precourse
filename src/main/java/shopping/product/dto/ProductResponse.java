package shopping.product.dto;

import shopping.product.domain.Product;

import java.util.List;

public record ProductResponse(Long id, String name, int price, String imageUrl) {

    public static ProductResponse from(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getPrice(),
                product.getImageUrl()
        );
    }

    public static List<ProductResponse> fromAll(List<Product> products) {
        return products.stream()
                .map(ProductResponse::from)
                .toList();
    }
}
