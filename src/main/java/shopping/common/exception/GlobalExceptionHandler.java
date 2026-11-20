package shopping.common.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.List;
import java.util.Map;


@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private static final String CODE = "code";
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    private static final Map<Class<? extends Exception>, ErrorCode> SPRING_ERROR_CODES = Map.of(
            MethodArgumentNotValidException.class, ErrorCode.INVALID_INPUT,
            HttpMessageNotReadableException.class, ErrorCode.INVALID_JSON,
            TypeMismatchException.class, ErrorCode.TYPE_MISMATCH,
            NoResourceFoundException.class, ErrorCode.NOT_EXIST_PATH,
            HttpRequestMethodNotSupportedException.class, ErrorCode.METHOD_NOT_ALLOWED,
            HttpMediaTypeNotSupportedException.class, ErrorCode.UNSUPPORTED_MEDIA_TYPE);

    @ExceptionHandler(BusinessException.class)
    public ProblemDetail handleBusiness(BusinessException exception) {
        return toProblemDetail(exception.getErrorCode());
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleUnexpected(Exception exception, HttpServletRequest request) {
        log.error("서버 오류가 발생했습니다. {} {}", request.getMethod(), request.getRequestURI(), exception);
        return toProblemDetail(ErrorCode.INTERNAL_ERROR);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException exception, HttpHeaders headers,
            HttpStatusCode statusCode, WebRequest request) {
        List<FieldErrorResponse> errors = exception.getBindingResult().getFieldErrors().stream()
                .map(this::toFieldErrorResponse)
                .toList();

        ProblemDetail problem = exception.getBody();
        problem.setProperty("errors", errors);
        return handleExceptionInternal(exception, problem, headers, statusCode, request);
    }

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(
            Exception exception, Object body,
            HttpHeaders headers, HttpStatusCode statusCode,
            WebRequest request) {
        ResponseEntity<Object> response =
                super.handleExceptionInternal(exception, body, headers, statusCode, request);

        ErrorCode errorCode = toErrorCode(exception);
        if (errorCode != null && response != null
                && response.getBody() instanceof ProblemDetail problem) {
            applyErrorCode(problem, errorCode);
        }
        return response;
    }

    private ErrorCode toErrorCode(Exception exception) {
        return SPRING_ERROR_CODES.entrySet().stream()
                .filter(entry -> entry.getKey().isInstance(exception))
                .map(Map.Entry::getValue)
                .findFirst()
                .orElse(null);
    }

    private ProblemDetail toProblemDetail(ErrorCode errorCode) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(errorCode.getStatus());
        applyErrorCode(problemDetail, errorCode);
        return problemDetail;
    }

    private void applyErrorCode(ProblemDetail problem, ErrorCode errorCode) {
        problem.setDetail(errorCode.getMessage());
        problem.setProperty(CODE, errorCode.name());
    }

    private FieldErrorResponse toFieldErrorResponse(FieldError fieldError) {
        String codeName = fieldError.getDefaultMessage();

        try {
            ErrorCode errorCode = ErrorCode.valueOf(codeName);
            return new FieldErrorResponse(fieldError.getField(),
                    errorCode.name(), errorCode.getMessage());
        } catch (IllegalArgumentException | NullPointerException exception) {
            return new FieldErrorResponse(fieldError.getField(),
                    ErrorCode.INVALID_INPUT.name(), codeName);
        }
    }
}
