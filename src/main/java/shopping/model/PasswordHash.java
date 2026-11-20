package shopping.model;

public record PasswordHash(String value) {
    public PasswordHash {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(
                    "비밀번호 해시는 비어 있을 수 없습니다");
        }
    }
}
