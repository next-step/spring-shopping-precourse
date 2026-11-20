package shopping.product.dto;

import shopping.product.entity.Product;

public record ProductResponseDto (Long id, String name, int price, int count, String url) {
    public static ProductResponseDto from(Product product) {
        return new ProductResponseDto(
                product.getId(),
                product.getName(),
                product.getPrice(),
                product.getCount(),
                product.getUrl()
        );
    }
}
