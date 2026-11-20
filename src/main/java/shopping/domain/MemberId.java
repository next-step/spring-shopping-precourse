package shopping.domain;

public record MemberId(long value) {
    public MemberId {
        validatePositive(value);
    }

    private static void validatePositive(long value) {
        if (value <= 0) {
            throw new IllegalArgumentException("회원 번호는 1 이상의 숫자여야 합니다.");
        }
    }
}
