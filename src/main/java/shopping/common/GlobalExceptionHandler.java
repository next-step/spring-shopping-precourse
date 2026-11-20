package shopping.common;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.RestClientException;
import shopping.auth.InvalidTokenException;
import shopping.member.DuplicateEmailException;
import shopping.member.LoginFailedException;
import shopping.product.ProductNotFoundException;
import shopping.wish.DuplicateWishException;
import shopping.wish.WishNotFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleBadRequest(IllegalArgumentException e) {
        return toResponse(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleUnreadable(HttpMessageNotReadableException e) {
        return toResponse(HttpStatus.BAD_REQUEST, "요청 본문이 올바른 JSON 형식이 아닙니다.");
    }

    @ExceptionHandler({ProductNotFoundException.class, WishNotFoundException.class})
    public ResponseEntity<ErrorResponse> handleNotFound(RuntimeException e) {
        return toResponse(HttpStatus.NOT_FOUND, e.getMessage());
    }

    @ExceptionHandler({DuplicateEmailException.class, DuplicateWishException.class})
    public ResponseEntity<ErrorResponse> handleConflict(RuntimeException e) {
        return toResponse(HttpStatus.CONFLICT, e.getMessage());
    }

    @ExceptionHandler({LoginFailedException.class, InvalidTokenException.class})
    public ResponseEntity<ErrorResponse> handleUnauthorized(RuntimeException e) {
        return toResponse(HttpStatus.UNAUTHORIZED, e.getMessage());
    }

    @ExceptionHandler(RestClientException.class)
    public ResponseEntity<ErrorResponse> handleExternalFailure(RestClientException e) {
        return toResponse(HttpStatus.SERVICE_UNAVAILABLE, "비속어 검사 서비스를 사용할 수 없습니다.");
    }

    private ResponseEntity<ErrorResponse> toResponse(HttpStatus status, String message) {
        return ResponseEntity.status(status).body(new ErrorResponse(message));
    }
}