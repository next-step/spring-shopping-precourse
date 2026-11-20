package shopping.model;

public record Email(String value) {
    public static final String PATTERN = "^[^\\s@]+@[^\\s@]+$";

    public Email {
        if (value == null || !value.matches(PATTERN)) {
            throw new IllegalArgumentException("올바른 이메일 형식이어야 합니다");
        }
    }
}
