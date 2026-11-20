package shopping.model;

public record Password(String value) {
    public Password {
        if (value == null) {
            throw new IllegalArgumentException("비밀번호는 null일 수 없습니다");
        }
    }
}
