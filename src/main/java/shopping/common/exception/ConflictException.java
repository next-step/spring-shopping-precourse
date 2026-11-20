package shopping.common.exception;

import org.springframework.http.HttpStatus;

// 이미 있는 것과 충돌할 때(409)
public class ConflictException extends ShoppingException {

    public ConflictException(String message) {
        super(HttpStatus.CONFLICT, message);
    }
}
