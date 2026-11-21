package shopping.product;

public record Price(int value) {

    private static final int MIN_PRICE = 1;

    public Price {
        validateMinimum(value);
    }

    private static void validateMinimum(int value) {
        if (value < MIN_PRICE) {
            throw new IllegalArgumentException("상품 가격은 " + MIN_PRICE + "원 이상이어야 합니다.");
        }
    }
}
