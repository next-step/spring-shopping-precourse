package shopping.product;

public class ProductPrice {

    private final int value;

    public ProductPrice(int value) {
        check(value);
        this.value = value;
    }

    private void check(int value) {
        if (value <= 0) {
            throw new IllegalArgumentException("가격은 0보다 커야 합니다.");
        }
    }

    public int getValue() {
        return value;
    }
}
