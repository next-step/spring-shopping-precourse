package shopping.product;

import java.util.regex.Pattern;

public class ProductName {
    private static final int MAX_LENGTH = 15;
    private static final Pattern ALLOWED_PATTERN =
            Pattern.compile("^[\\p{L}\\p{N} ()\\[\\]+\\-&/_]+$");

    private final String value;

    public ProductName(String value) {
        validateNotBlank(value);
        validateLength(value);
        validateCharacters(value);
        this.value = value;
    }

    private void validateNotBlank(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("상품 이름은 비어 있을 수 없습니다.");
        }
    }

    private void validateLength(String value) {
        if (value.length() > MAX_LENGTH) {
            throw new IllegalArgumentException("상품 이름은 공백 포함 최대 15자입니다.");
        }
    }

    private void validateCharacters(String value) {
        if (!ALLOWED_PATTERN.matcher(value).matches()) {
            throw new IllegalArgumentException("상품 이름에 사용할 수 없는 특수 문자가 있습니다.");
        }
    }

    public String getValue() {
        return value;
    }
}