package shopping.product;

public class ProductImageUrl {

    private final String value;

    public ProductImageUrl(String value) {
        check(value);
        this.value = value;
    }

    private void check(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("사진 주소는 비어 있을 수 없습니다.");
        }
    }

    public String getValue() {
        return value;
    }
}
