package shopping.domain;

public record ProductId(long value) {
    public ProductId {
        validatePositive(value);
    }

    private static void validatePositive(long value) {
        if (value <= 0) {
            throw new IllegalArgumentException("상품 ID는 1 이상이어야 합니다.");
        }
    }
}
