package shopping.domain;

public class ProductPrice {
    private final long value;

    public ProductPrice(long value) {
        validateNotNegative(value);
        this.value = value;
    }

    private void validateNotNegative(long value) {
        if (value < 0) {
            throw new IllegalArgumentException("상품 가격은 0원 이상이어야 합니다.");
        }
    }

    public long value() {
        return value;
    }
}
