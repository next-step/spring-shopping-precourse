package shopping;

final class InvalidTokenException extends IllegalArgumentException {
    InvalidTokenException() {
        super("유효하지 않은 인증 토큰입니다");
    }
}
