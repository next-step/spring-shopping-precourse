package shopping.common.exception;

import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.BAD_GATEWAY;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

public enum ErrorCode {

    PRODUCT_NAME_TOO_LONG(BAD_REQUEST, "상품 이름은 15자 이하여야 합니다."),
    NOT_ALLOWED_CHARACTERS(BAD_REQUEST, "특수 문자가 들어갈 수 없습니다."),
    PRICE_MISSING(BAD_REQUEST, "가격은 필수로 입력해야 합니다."),
    EXCEED_PRICE(BAD_REQUEST, "가격 범위는 0 ~ 2,147,483,647이어야 합니다."),
    EMAIL_MISSING(BAD_REQUEST, "이메일은 필수로 입력해야 합니다."),
    PASSWORD_MISSING(BAD_REQUEST, "비밀번호는 필수로 입력해야 합니다."),
    NAME_MISSING(BAD_REQUEST, "이름은 필수로 입력해야 합니다."),
    INVALID_PASSWORD_LENGTH(BAD_REQUEST, "비밀번호는 15자 이상 72자 이하여야 합니다."),
    NAME_TOO_LONG(BAD_REQUEST, "이름은 39자 이하여야 합니다."),
    INVALID_PASSWORD_CHARACTER(BAD_REQUEST, "비밀번호는 영문, 숫자, 특수 문자만 사용할 수 있습니다."),
    INVALID_EMAIL_FORMAT(BAD_REQUEST, "이메일 형식이 올바르지 않습니다."),
    FORBIDDEN_PRODUCT_NAME(BAD_REQUEST, "비속어는 포함될 수 없습니다."),
    DUPLICATE_EMAIL(CONFLICT, "중복된 이메일 입니다."),
    NOT_EXIST_PRODUCT(NOT_FOUND, "존재하지 않는 상품입니다."),
    DUPLICATE_WISH_APPLY(CONFLICT, "같은 상품을 두 번 담을 수 없습니다."),
    NOT_EXIST_WISH(NOT_FOUND, "존재하지 않는 위시리스트 입니다."),
    FAIL_CALL_PURGOMALUM(BAD_GATEWAY, "API 호출에 실패했습니다."),
    FAIL_LOGIN(UNAUTHORIZED, "이메일 또는 비밀번호가 올바르지 않습니다."),
    INVALID_TOKEN(UNAUTHORIZED, "인증 토큰이 없거나 유효하지 않습니다."),
    INVALID_INPUT(BAD_REQUEST, "입력 값이 올바르지 않습니다."),
    INVALID_JSON(BAD_REQUEST, "요청 형식의 값이 올바르지 않습니다."),
    TYPE_MISMATCH(BAD_REQUEST, "값의 형식이 올바르지 않습니다."),
    NOT_EXIST_PATH(NOT_FOUND, "존재하지 않는 경로입니다."),
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "지원하지 않는 요청 방식입니다."),
    UNSUPPORTED_MEDIA_TYPE(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "지원하지 않는 요청 형식입니다."),
    INTERNAL_ERROR(INTERNAL_SERVER_ERROR, "서버 오류가 발생했습니다."),
    PRODUCT_NAME_MISSING(BAD_REQUEST, "상품 이름은 필수로 입력해야 합니다."),
    URL_MISSING(BAD_REQUEST, "이미지 URL은 필수로 입력해야 합니다."),
    INVALID_URL_FORMAT(BAD_REQUEST, "이미지 URL은 http:// 또는 https://로 시작해야 합니다."),
    PRODUCT_ID_MISSING(BAD_REQUEST, "상품은 필수로 입력해야 합니다.");

    private final HttpStatus status;
    private final String message;

    ErrorCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }
}
