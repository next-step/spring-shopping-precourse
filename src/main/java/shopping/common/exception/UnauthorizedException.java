package shopping.common.exception;

import org.springframework.http.HttpStatus;

// 인증에 실패했을 때(401)
public class UnauthorizedException extends ShoppingException {

    public UnauthorizedException(String message) {
        super(HttpStatus.UNAUTHORIZED, message);
    }
}
