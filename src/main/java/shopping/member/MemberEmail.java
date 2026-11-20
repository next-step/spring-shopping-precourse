package shopping.member;

public class MemberEmail {
    private static final String FORM = "^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$";

    private final String value;

    public MemberEmail(String value) {
        checkBlank(value);
        checkForm(value);
        this.value = value;
    }

    private void checkBlank(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("이메일은 비어 있을 수 없습니다.");
        }
    }

    private void checkForm(String value) {
        if (!value.matches(FORM)) {
            throw new IllegalArgumentException("이메일 형식이 올바르지 않습니다.");
        }
    }

    public String getValue() {
        return value;
    }
}
