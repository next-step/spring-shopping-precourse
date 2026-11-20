package shopping.common.exception;

import org.springframework.http.HttpStatus;

// 대상을 찾을 수 없을 때(404)
public class NotFoundException extends ShoppingException {

    public NotFoundException(String message) {
        super(HttpStatus.NOT_FOUND, message);
    }
}
