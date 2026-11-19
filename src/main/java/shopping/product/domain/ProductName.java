package shopping.product.domain;

import java.util.regex.Pattern;

public class ProductName {

    private static final int MAX_LENGTH = 15;
    private static final Pattern ALLOWED_CHARACTERS =
            Pattern.compile("^[\\p{L}\\p{N} ()\\[\\]+\\-&/_]+$");

    private final String value;

    public ProductName(String value) {
        validate(value);
        this.value = value;
    }

    private void validate(String value) {
        validateNotBlank(value);
        validateLength(value);
        validateCharacters(value);
    }

    private void validateNotBlank(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("상품 이름은 비어 있을 수 없습니다.");
        }
    }

    private void validateLength(String value) {
        if (value.length() > MAX_LENGTH) {
            throw new IllegalArgumentException(
                    "상품 이름은 공백을 포함하여 최대 " + MAX_LENGTH + "자까지 입력할 수 있습니다.");
        }
    }

    private void validateCharacters(String value) {
        if (!ALLOWED_CHARACTERS.matcher(value).matches()) {
            throw new IllegalArgumentException(
                    "상품 이름에는 ( ), [ ], +, -, &, /, _ 외의 특수 문자를 사용할 수 없습니다.");
        }
    }

    public String getValue() {
        return value;
    }
}
