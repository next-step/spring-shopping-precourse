package shopping.domain;

public record ProductId(long value) {
    public ProductId {
        validatePositive(value);
    }

    private static void validatePositive(long value) {
        if (value <= 0) {
            throw new IllegalArgumentException("상품 번호는 1 이상의 숫자여야 합니다.");
        }
    }
}
