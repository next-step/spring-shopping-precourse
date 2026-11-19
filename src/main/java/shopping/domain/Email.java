package shopping.domain;

public class Email {
    private static final char AT = '@';
    private static final String ALLOWED_DOMAIN = "gmail.com";

    private final String value;

    public Email(String value) {
        validateNotBlank(value);
        validateNoWhitespace(value);
        validateSingleAt(value);
        validateLocalPart(value);
        validateDomain(value);
        this.value = value;
    }

    private void validateNotBlank(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("이메일은 비어 있을 수 없습니다.");
        }
    }

    private void validateNoWhitespace(String value) {
        if (value.chars().anyMatch(Character::isWhitespace)) {
            throw new IllegalArgumentException("이메일에는 공백 문자를 사용할 수 없습니다.");
        }
    }

    private void validateSingleAt(String value) {
        long count = value.chars().filter(character -> character == AT).count();
        if (count != 1) {
            throw new IllegalArgumentException("이메일에는 @가 정확히 하나 있어야 합니다.");
        }
    }

    private void validateLocalPart(String value) {
        if (value.indexOf(AT) == 0) {
            throw new IllegalArgumentException("이메일의 @ 앞이 비어 있을 수 없습니다.");
        }
    }

    private void validateDomain(String value) {
        String domain = value.substring(value.indexOf(AT) + 1);
        if (!ALLOWED_DOMAIN.equalsIgnoreCase(domain)) {
            throw new IllegalArgumentException("이메일은 gmail.com만 사용할 수 있습니다.");
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
        return other instanceof Email email && value.equals(email.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}
