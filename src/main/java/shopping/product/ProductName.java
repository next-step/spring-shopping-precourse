package shopping.product;

import java.util.regex.Pattern;

public record ProductName(String value) {

    private static final int MAX_LENGTH = 15;
    private static final Pattern ALLOWED_CHARACTERS =
            Pattern.compile("[\\p{L}0-9 ()\\[\\]+\\-&/_]+");

    public ProductName {
        validateNotBlank(value);
        validateLength(value);
        validateCharacters(value);
    }

    private static void validateNotBlank(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("상품 이름은 비어 있을 수 없습니다.");
        }
    }

    private static void validateLength(String value) {
        if (value.length() > MAX_LENGTH) {
            throw new IllegalArgumentException(
                    "상품 이름은 공백 포함 최대 " + MAX_LENGTH + "자까지 입력할 수 있습니다.");
        }
    }

    private static void validateCharacters(String value) {
        if (!ALLOWED_CHARACTERS.matcher(value).matches()) {
            throw new IllegalArgumentException(
                    "상품 이름에는 ( ), [ ], +, -, &, /, _ 외의 특수문자를 사용할 수 없습니다.");
        }
    }
}
