package shopping.common.exception;

import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import shopping.auth.exception.InvalidTokenException;
import shopping.member.exception.DuplicateEmailException;
import shopping.member.exception.LoginFailedException;
import shopping.product.exception.ProductNotFoundException;
import shopping.product.exception.ProfanityProductNameException;
import shopping.wish.exception.DuplicateWishException;
import shopping.wish.exception.ForbiddenWishException;
import shopping.wish.exception.WishNotFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException exception) {
        String message = exception.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .orElse("잘못된 요청입니다.");
        return response(HttpStatus.BAD_REQUEST, message);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleUnreadableRequest() {
        return response(HttpStatus.BAD_REQUEST, "잘못된 요청입니다.");
    }

    @ExceptionHandler(ProfanityProductNameException.class)
    public ResponseEntity<ErrorResponse> handleBadRequest(RuntimeException exception) {
        return response(HttpStatus.BAD_REQUEST, exception.getMessage());
    }

    @ExceptionHandler({InvalidTokenException.class, LoginFailedException.class})
    public ResponseEntity<ErrorResponse> handleUnauthorized(RuntimeException exception) {
        return response(HttpStatus.UNAUTHORIZED, exception.getMessage());
    }

    @ExceptionHandler(ForbiddenWishException.class)
    public ResponseEntity<ErrorResponse> handleForbidden(RuntimeException exception) {
        return response(HttpStatus.FORBIDDEN, exception.getMessage());
    }

    @ExceptionHandler({ProductNotFoundException.class, WishNotFoundException.class})
    public ResponseEntity<ErrorResponse> handleNotFound(RuntimeException exception) {
        return response(HttpStatus.NOT_FOUND, exception.getMessage());
    }

    @ExceptionHandler({DuplicateEmailException.class, DuplicateWishException.class})
    public ResponseEntity<ErrorResponse> handleConflict(RuntimeException exception) {
        return response(HttpStatus.CONFLICT, exception.getMessage());
    }

    private ResponseEntity<ErrorResponse> response(HttpStatus status, String message) {
        return ResponseEntity.status(status).body(new ErrorResponse(message));
    }
}
