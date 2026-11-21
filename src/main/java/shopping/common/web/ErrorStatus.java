package shopping.common.web;

import java.util.Map;
import org.springframework.http.HttpStatus;
import shopping.common.error.ErrorCode;

final class ErrorStatus {

    private static final Map<ErrorCode, HttpStatus> STATUSES = Map.ofEntries(
            Map.entry(ErrorCode.PROFANITY_DETECTED, HttpStatus.BAD_REQUEST),
            Map.entry(ErrorCode.PROFANITY_CHECK_UNAVAILABLE, HttpStatus.SERVICE_UNAVAILABLE),
            Map.entry(ErrorCode.INVALID_CREDENTIALS, HttpStatus.UNAUTHORIZED),
            Map.entry(ErrorCode.PRODUCT_NOT_FOUND, HttpStatus.NOT_FOUND),
            Map.entry(ErrorCode.WISH_ALREADY_EXISTS, HttpStatus.CONFLICT),
            Map.entry(ErrorCode.FORBIDDEN, HttpStatus.FORBIDDEN),
            Map.entry(ErrorCode.WISH_NOT_FOUND, HttpStatus.NOT_FOUND),
            Map.entry(ErrorCode.INVALID_TOKEN, HttpStatus.UNAUTHORIZED),
            Map.entry(ErrorCode.INVALID_REQUEST, HttpStatus.BAD_REQUEST),
            Map.entry(ErrorCode.EMAIL_ALREADY_EXISTS, HttpStatus.CONFLICT));

    private ErrorStatus() {
    }

    static HttpStatus of(ErrorCode code) {
        return STATUSES.getOrDefault(code, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    static ErrorCode from(int status) {
        return Map.of(400, ErrorCode.INVALID_REQUEST, 404, ErrorCode.ROUTE_NOT_FOUND,
                405, ErrorCode.METHOD_NOT_ALLOWED, 406, ErrorCode.NOT_ACCEPTABLE,
                415, ErrorCode.UNSUPPORTED_MEDIA_TYPE)
                .getOrDefault(status, ErrorCode.INTERNAL_SERVER_ERROR);
    }
}
