package shopping.controller;

import java.util.Map;
import java.util.NoSuchElementException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import shopping.domain.ProfanityCheckException;
import shopping.domain.ProfanityCheckException.Reason;
import shopping.service.LoginFailedException;
import shopping.service.UnauthorizedException;

@RestControllerAdvice
public class ApiExceptionHandler {
    private record Failure(HttpStatus status, String message) {
    }

    private static final Map<Reason, Failure> PROFANITY_CHECK_FAILURES = Map.of(
            Reason.BAD_REQUEST, new Failure(HttpStatus.INTERNAL_SERVER_ERROR,
                    "비속어 검사 요청에 문제가 있어 처리할 수 없습니다."),
            Reason.SERVICE_ERROR, new Failure(HttpStatus.SERVICE_UNAVAILABLE,
                    "비속어 검사 서비스를 일시적으로 사용할 수 없습니다. 잠시 후 다시 시도해 주세요."),
            Reason.CONNECTION_ERROR, new Failure(HttpStatus.SERVICE_UNAVAILABLE,
                    "비속어 검사 서비스에 연결할 수 없습니다. 잠시 후 다시 시도해 주세요."),
            Reason.INVALID_RESPONSE, new Failure(HttpStatus.BAD_GATEWAY,
                    "비속어 검사 서비스에서 올바르지 않은 응답을 받았습니다."));

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> badRequest(IllegalArgumentException e) {
        return respond(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(LoginFailedException.class)
    public ResponseEntity<ErrorResponse> loginFailed(LoginFailedException e) {
        return respond(HttpStatus.UNAUTHORIZED, e.getMessage());
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ErrorResponse> unauthorized(UnauthorizedException e) {
        return respond(HttpStatus.UNAUTHORIZED, e.getMessage());
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ErrorResponse> notFound(NoSuchElementException e) {
        return respond(HttpStatus.NOT_FOUND, e.getMessage());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> unreadableBody(HttpMessageNotReadableException e) {
        return respond(HttpStatus.BAD_REQUEST, "요청 내용을 읽을 수 없습니다. 입력한 내용을 확인해 주세요.");
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> invalidPathValue(MethodArgumentTypeMismatchException e) {
        return respond(HttpStatus.BAD_REQUEST, "주소에 입력한 번호가 올바르지 않습니다.");
    }

    // 내부 상세 원인(주소, 상태 코드 등)은 응답에 넣지 않는다. 예외 메시지에만 남아 있다.
    @ExceptionHandler(ProfanityCheckException.class)
    public ResponseEntity<ErrorResponse> profanityCheckFailed(ProfanityCheckException e) {
        Failure failure = PROFANITY_CHECK_FAILURES.get(e.reason());
        return respond(failure.status(), failure.message());
    }

    private ResponseEntity<ErrorResponse> respond(HttpStatus status, String message) {
        return ResponseEntity.status(status).body(new ErrorResponse(message));
    }
}
