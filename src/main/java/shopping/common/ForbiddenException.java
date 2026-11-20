package shopping.common;

import org.springframework.http.HttpStatus;

public class ForbiddenException extends ShoppingException {
    public ForbiddenException(String message) {
        super(HttpStatus.FORBIDDEN, message);
    }
}
