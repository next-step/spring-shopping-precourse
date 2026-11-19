package shopping.product.domain;

public class ProductPrice {

    private final int value;

    public ProductPrice(Integer value) {
        validate(value);
        this.value = value;
    }

    private void validate(Integer value) {
        if (value == null || value <= 0) {
            throw new IllegalArgumentException("상품 가격은 0보다 커야 합니다.");
        }
    }

    public int getValue() {
        return value;
    }
}
