package shopping.product;

public record ImageUrl(String value) {

    public ImageUrl {
        validateNotBlank(value);
    }

    private static void validateNotBlank(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("상품 이미지 URL은 비어 있을 수 없습니다.");
        }
    }
}
