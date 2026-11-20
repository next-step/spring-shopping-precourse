package shopping.shared.web;

import java.util.Map;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import shopping.member.web.MemberController;
import shopping.shared.application.RequestFailure;
import shopping.shared.application.RequestFailure.Reason;
import shopping.wish.web.WishController;

@RestControllerAdvice(assignableTypes = {MemberController.class, WishController.class})
public class ShoppingErrorHandler {
    private static final Map<Reason, HttpStatus> STATUSES = Map.of(
            Reason.INVALID_INPUT, HttpStatus.BAD_REQUEST,
            Reason.INVALID_CREDENTIALS, HttpStatus.UNAUTHORIZED,
            Reason.AUTHENTICATION_REQUIRED, HttpStatus.UNAUTHORIZED,
            Reason.NOT_FOUND, HttpStatus.NOT_FOUND,
            Reason.CONFLICT, HttpStatus.CONFLICT);

    @ExceptionHandler(RequestFailure.class)
    public ResponseEntity<ErrorResponse> requestFailure(RequestFailure failure) {
        ResponseEntity.BodyBuilder response = ResponseEntity.status(STATUSES.get(failure.reason()));
        if (failure.reason() == Reason.AUTHENTICATION_REQUIRED) {
            response.header(HttpHeaders.WWW_AUTHENTICATE, "Bearer");
        }
        return response.body(new ErrorResponse(failure.getMessage()));
    }

    @ExceptionHandler({HttpMessageNotReadableException.class, MethodArgumentTypeMismatchException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse invalidRequest() {
        return new ErrorResponse("요청의 JSON 형식과 필드 값을 확인해 주세요.");
    }

    public record ErrorResponse(String message) {}
}
