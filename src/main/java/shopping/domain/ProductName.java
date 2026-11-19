package shopping.domain;

import java.util.regex.Pattern;

public class ProductName {
    private static final int MAX_LENGTH = 15;
    private static final Pattern ALLOWED = Pattern.compile("[A-Za-z0-9 ()\\[\\]+\\-&/_]+");

    private final String value;

    public ProductName(String value) {
        validateNotBlank(value);
        validateLength(value);
        validateCharacters(value);
        this.value = value;
    }

    private void validateCharacters(String value) {
        if (!ALLOWED.matcher(value).matches()) {
            throw new IllegalArgumentException("상품 이름에는 영문, 숫자, 공백, ()[]+-&/_ 만 사용할 수 있습니다.");
        }
    }

    private void validateNotBlank(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("상품 이름은 비어 있을 수 없습니다.");
        }
    }

    private void validateLength(String value) {
        if (value.length() > MAX_LENGTH) {
            throw new IllegalArgumentException("상품 이름은 공백 포함 15자 이하여야 합니다.");
        }
    }

    public String value() {
        return value;
    }
}
