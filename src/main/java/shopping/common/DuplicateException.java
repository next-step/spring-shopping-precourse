package shopping.common;

import org.springframework.http.HttpStatus;

public class DuplicateException extends ShoppingException {
    public DuplicateException(String message) {
        super(HttpStatus.CONFLICT, message);
    }
}
