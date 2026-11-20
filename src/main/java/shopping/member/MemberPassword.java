package shopping.member;

public class MemberPassword {

    private final String value;

    public MemberPassword(String value) {
        checkBlank(value);
        this.value = value;
    }

    private void checkBlank(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("비밀번호는 비어 있을 수 없습니다.");
        }
    }

    public String getValue() {
        return value;
    }
}
