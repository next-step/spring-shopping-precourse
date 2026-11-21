package shopping.auth.exception;

/**
 * 인증 토큰이 없거나 유효하지 않을 때 발생하는 예외.
 * (서명 불일치, 만료, 형식 오류 등)
 */
public class InvalidTokenException extends RuntimeException {

    public InvalidTokenException() {
        super("유효하지 않은 인증 토큰입니다.");
    }
}
