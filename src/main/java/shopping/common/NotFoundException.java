package shopping.common;

import org.springframework.http.HttpStatus;

public class NotFoundException extends ShoppingException {
    public NotFoundException(String message) {
        super(HttpStatus.NOT_FOUND, message);
    }
}
