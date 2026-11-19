package shopping.product.dto;

import shopping.product.domain.Product;
import shopping.product.domain.ProductName;
import shopping.product.domain.ProductPrice;

public record ProductCreateRequest(String name, Integer price, String imageUrl) {

    public Product toEntity() {
        return new Product(new ProductName(name), new ProductPrice(price), imageUrl);
    }
}
