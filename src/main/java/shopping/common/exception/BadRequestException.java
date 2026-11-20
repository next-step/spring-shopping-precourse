package shopping.common.exception;

import org.springframework.http.HttpStatus;

// 요청 값이 잘못됐을 때(400). 
public class BadRequestException extends ShoppingException {

    public BadRequestException(String message) {
        super(HttpStatus.BAD_REQUEST, message);
    }
}
