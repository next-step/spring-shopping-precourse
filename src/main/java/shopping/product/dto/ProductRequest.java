package shopping.product.dto;

import shopping.product.ImageUrl;
import shopping.product.Price;
import shopping.product.Product;
import shopping.product.ProductName;

public record ProductRequest(String name, Integer price, String imageUrl) {
    public Product toEntity() {
        return new Product(new ProductName(name), new Price(price), new ImageUrl(imageUrl));
    }
}
