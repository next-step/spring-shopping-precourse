package shopping.product.dto;

import shopping.product.domain.ProductName;
import shopping.product.domain.ProductPrice;

public record ProductUpdateRequest(String name, Integer price, String imageUrl) {

    public ProductName toProductName() {
        return new ProductName(name);
    }

    public ProductPrice toProductPrice() {
        return new ProductPrice(price);
    }
}
