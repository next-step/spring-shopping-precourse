package shopping.domain;

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
        validateNotNull(id, "상품 번호가 없습니다.");
        this.id = id;
        assign(name, price, image);
    }

    public void update(ProductName name, ProductPrice price, ProductImage image) {
        assign(name, price, image);
    }

    private void assign(ProductName name, ProductPrice price, ProductImage image) {
        validateNotNull(name, "상품 이름 정보가 없습니다.");
        validateNotNull(price, "상품 가격 정보가 없습니다.");
        validateNotNull(image, "상품 이미지 정보가 없습니다.");
        this.name = name;
        this.price = price;
        this.image = image;
    }

    public boolean hasName(ProductName other) {
        validateNotNull(other, "비교할 상품 이름이 없습니다.");
        return name.equals(other);
    }

    public boolean hasId(ProductId other) {
        validateNotNull(other, "비교할 상품 번호가 없습니다.");
        return other.equals(id);
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

    private static void validateNotNull(Object value, String message) {
        if (value == null) {
            throw new NullPointerException(message);
        }
    }
}
