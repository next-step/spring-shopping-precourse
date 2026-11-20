package shopping.common;

import org.springframework.http.HttpStatus;

public class UnauthorizedException extends ShoppingException {
    public UnauthorizedException(String message) {
        super(HttpStatus.UNAUTHORIZED, message);
    }
}
