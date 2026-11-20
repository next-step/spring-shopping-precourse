package shopping.common.exception;

import org.springframework.http.HttpStatus;

// 인증은 됐지만 권한이 없을 때(403)
public class ForbiddenException extends ShoppingException {

    public ForbiddenException(String message) {
        super(HttpStatus.FORBIDDEN, message);
    }
}
