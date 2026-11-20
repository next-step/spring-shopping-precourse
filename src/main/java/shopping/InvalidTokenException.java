package shopping;

public class InvalidTokenException extends RuntimeException {
    public InvalidTokenException() {
        super("인증 토큰이 없거나 유효하지 않습니다.");
    }
}
