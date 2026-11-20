package shopping.domain;

import java.util.regex.Pattern;

public class ProductName {
    private static final int MAX_LENGTH = 15;
    private static final Pattern ALLOWED = Pattern.compile("[A-Za-z0-9 ()\\[\\]+\\-&/_]+");

    private final String value;

    public ProductName(String value, ProfanityChecker profanityChecker) {
        validateNotNull(profanityChecker, "비속어 검사기가 설정되지 않았습니다.");
        validateNotBlank(value);
        validateLength(value);
        validateCharacters(value);
        validateProfanity(value, profanityChecker);
        this.value = value;
    }

    private void validateProfanity(String value, ProfanityChecker profanityChecker) {
        if (profanityChecker.containsProfanity(value)) {
            throw new IllegalArgumentException("상품 이름에 비속어가 포함될 수 없습니다.");
        }
    }

    private void validateCharacters(String value) {
        if (!ALLOWED.matcher(value).matches()) {
            throw new IllegalArgumentException(
                    "상품 이름에는 영문, 숫자, 띄어쓰기와 특수문자 ( ) [ ] + - & / _ 만 사용할 수 있습니다.");
        }
    }

    private void validateNotBlank(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("상품 이름을 입력해 주세요.");
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

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        return other instanceof ProductName name && value.equals(name.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    private static void validateNotNull(Object value, String message) {
        if (value == null) {
            throw new NullPointerException(message);
        }
    }
}
