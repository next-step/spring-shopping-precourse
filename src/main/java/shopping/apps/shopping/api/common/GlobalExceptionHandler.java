package shopping.apps.shopping.api.common;

import com.auth0.jwt.exceptions.TokenExpiredException;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import shopping.domains.common.core.domain.entity.*;
import shopping.domains.common.core.domain.enums.CommonErrorCode;
import shopping.domains.common.core.domain.enums.ErrorCode;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler(ClientIllegalArgumentException.class)
    public ResponseEntity<Object> handleClientIllegalArgument(@NonNull final ClientIllegalArgumentException e) {
        log.warn("handleResourceAlreadyExist", e);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(makeErrorResponse(e.getErrorCode()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Object> handleIllegalArgument(@NonNull final IllegalArgumentException e) {
        log.warn("handleResourceAlreadyExist", e);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(makeErrorResponse(CommonErrorCode.BAD_REQUEST));
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<Object> handleClientException(@NonNull final UnauthorizedException e) {
        log.warn("ClientException", e);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(makeErrorResponse(e.getErrorCode()));
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Object> handleResourceNotFound(@NonNull final ResourceNotFoundException e) {
        log.warn("handleResourceNotFound", e);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(makeErrorResponse(e.getErrorCode()));
    }

    @ExceptionHandler(AlreadyExistResourceException.class)
    public ResponseEntity<Object> handleAlreadyExistResource(@NonNull final AlreadyExistResourceException e) {
        log.warn("handleAlreadyExistResourceException", e);
        return ResponseEntity.status(HttpStatus.CONFLICT).body(makeErrorResponse(e.getErrorCode()));
    }


    @ExceptionHandler(ObjectOptimisticLockingFailureException.class)
    public ResponseEntity<Object> handleOptimisticLocking(ObjectOptimisticLockingFailureException e) {
        log.warn("ObjectOptimisticLockingFailureException", e);
        return ResponseEntity.status(HttpStatus.CONFLICT).body(makeErrorResponse(CommonErrorCode.DUPLICATE));
    }

    @ExceptionHandler(ThirdPartyUnavailableException.class)
    public ResponseEntity<Object> handleThirdPartyUnavailable(@NonNull final ThirdPartyUnavailableException e) {
        log.warn("ThirdPartyUnavailableException", e);
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(makeErrorResponse(CommonErrorCode.SERVICE_UNAVAILABLE));
    }

    @ExceptionHandler(TokenExpiredException.class)
    public ResponseEntity<Object> handleTokenExpired(@NonNull final TokenExpiredException e) {
        log.warn("TokenExpiredException", e);
        return ResponseEntity.status(401).body(makeErrorResponse(CommonErrorCode.UNAUTHORIZED));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleException(Exception e) {
        log.warn("handleException", e);
        return ResponseEntity.status(500).body(makeErrorResponse(CommonErrorCode.INTERNAL_SERVER_ERROR));
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            @NonNull final MethodArgumentNotValidException ex,
            @NonNull final HttpHeaders headers,
            @NonNull final HttpStatusCode status,
            @NonNull final WebRequest request
    ) {

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(makeErrorResponse(CommonErrorCode.BAD_REQUEST));
    }

    private ErrorResponse makeErrorResponse(@NonNull final ErrorCode errorCode) {
        return new ErrorResponse(errorCode.getCode(), errorCode.getMessage());
    }
}

record ErrorResponse(String code, String message) {
}
