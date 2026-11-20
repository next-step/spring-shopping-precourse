package shopping.domain;

public class Email {
    private static final char AT = '@';
    private static final String ALLOWED_DOMAIN = "gmail.com";

    private final String value;

    public Email(String value) {
        validateNotBlank(value);
        validateNoWhitespace(value);
        validateSingleAt(value);
        validateUsernameNotEmpty(value);
        validateDomain(value);
        this.value = value;
    }

    private void validateNotBlank(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("이메일을 입력해 주세요.");
        }
    }

    private void validateNoWhitespace(String value) {
        if (value.chars().anyMatch(Character::isWhitespace)) {
            throw new IllegalArgumentException("이메일에는 띄어쓰기를 사용할 수 없습니다.");
        }
    }

    private void validateSingleAt(String value) {
        long count = value.chars().filter(character -> character == AT).count();
        if (count != 1) {
            throw new IllegalArgumentException("이메일 형식이 올바르지 않습니다. @가 하나만 있어야 합니다.");
        }
    }

    private void validateUsernameNotEmpty(String value) {
        if (value.indexOf(AT) == 0) {
            throw new IllegalArgumentException("이메일의 @ 앞에 아이디를 입력해 주세요.");
        }
    }

    private void validateDomain(String value) {
        String domain = value.substring(value.indexOf(AT) + 1);
        if (!ALLOWED_DOMAIN.equalsIgnoreCase(domain)) {
            throw new IllegalArgumentException("gmail.com 이메일만 사용할 수 있습니다.");
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
