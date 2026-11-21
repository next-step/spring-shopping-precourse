package shopping.model;

public record AuthToken(String value, Long memberId) {
    public AuthToken {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("토큰은 비어 있을 수 없습니다");
        }
        if (memberId == null) {
            throw new IllegalArgumentException("토큰의 회원 ID는 null일 수 없습니다");
        }
    }
}
