package shopping.common.web;

import java.util.Locale;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import shopping.common.error.ErrorCode;
import shopping.common.error.ShoppingException;

@RestControllerAdvice
public class ErrorHandler extends ResponseEntityExceptionHandler {

    private static final Logger LOG = LoggerFactory.getLogger(ErrorHandler.class);

    @ExceptionHandler(ShoppingException.class)
    public ResponseEntity<Object> handleShopping(ShoppingException error) {
        return respond(error.code());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Object> handleValidation(IllegalArgumentException error) {
        return respond(ErrorCode.INVALID_REQUEST);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Object> handleConstraint(DataIntegrityViolationException error) {
        String message = String.valueOf(error.getMostSpecificCause().getMessage());
        ErrorCode code = constraintCode(message.toLowerCase(Locale.ROOT));
        if (code == ErrorCode.INTERNAL_SERVER_ERROR) {
            return handleUnexpected(error);
        }

        return respond(code);
    }

    private ErrorCode constraintCode(String message) {
        if (message.contains("uq_members_email")) {
            return ErrorCode.EMAIL_ALREADY_EXISTS;
        }
        if (message.contains("uq_wishes_member_product")) {
            return ErrorCode.WISH_ALREADY_EXISTS;
        }
        if (message.contains("fk_wishes_product")) {
            return ErrorCode.PRODUCT_NOT_FOUND;
        }
        return ErrorCode.INTERNAL_SERVER_ERROR;
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleUnexpected(Exception error) {
        LOG.error("Request failed: {}", error.getClass().getSimpleName());
        return respond(ErrorCode.INTERNAL_SERVER_ERROR);
    }

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception error, Object body,
            HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.putAll(headers);
        responseHeaders.setContentType(MediaType.APPLICATION_JSON);
        return new ResponseEntity<>(ErrorResponse.of(ErrorStatus.from(status.value())),
                responseHeaders, status);
    }

    private ResponseEntity<Object> respond(ErrorCode code) {
        HttpStatus status = ErrorStatus.of(code);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        if (status == HttpStatus.UNAUTHORIZED) {
            headers.set(HttpHeaders.WWW_AUTHENTICATE, "Bearer");
        }

        return new ResponseEntity<>(ErrorResponse.of(code), headers, status);
    }
}
