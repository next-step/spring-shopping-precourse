package shopping.domain;

import java.util.Objects;
import java.util.Optional;

public class Product {
    private final ProductId id;
    private ProductName name;
    private ProductPrice price;
    private ProductImage image;

    public Product(ProductName name, ProductPrice price, ProductImage image) {
        this.id = null;
        assign(name, price, image);
    }

    public Product(ProductId id, ProductName name, ProductPrice price, ProductImage image) {
        this.id = Objects.requireNonNull(id, "상품 ID는 null일 수 없습니다.");
        assign(name, price, image);
    }

    public void update(ProductName name, ProductPrice price, ProductImage image) {
        assign(name, price, image);
    }

    private void assign(ProductName name, ProductPrice price, ProductImage image) {
        Objects.requireNonNull(name, "상품 이름은 null일 수 없습니다.");
        Objects.requireNonNull(price, "상품 가격은 null일 수 없습니다.");
        Objects.requireNonNull(image, "상품 이미지는 null일 수 없습니다.");
        this.name = name;
        this.price = price;
        this.image = image;
    }

    public Optional<ProductId> id() {
        return Optional.ofNullable(id);
    }

    public ProductName name() {
        return name;
    }

    public ProductPrice price() {
        return price;
    }

    public ProductImage image() {
        return image;
    }
}
