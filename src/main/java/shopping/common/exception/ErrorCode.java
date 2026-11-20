package shopping.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    DUPLICATE_EMAIL(HttpStatus.CONFLICT, "이미 가입된 계정입니다."),
    AUTHENTICATION_FAILED(HttpStatus.UNAUTHORIZED, "이메일 또는 비밀번호가 올바르지 않습니다."),
    PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND, "상품이 존재하지 않습니다."),
    DUPLICATE_PRODUCT(HttpStatus.CONFLICT, "이미 등록된 상품 이름입니다."),
    PRODUCT_NAME_PROFANITY(HttpStatus.BAD_REQUEST, "상품 이름에 비속어가 포함될 수 없습니다."),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "인증이 필요합니다."),
    WISH_FORBIDDEN(HttpStatus.FORBIDDEN, "본인의 위시리스트만 삭제할 수 있습니다."),
    WISH_NOT_FOUND(HttpStatus.NOT_FOUND, "위시리스트 항목이 존재하지 않습니다."),
    DUPLICATE_WISH(HttpStatus.CONFLICT, "이미 위시리스트에 담긴 상품입니다."),
    INVALID_REQUEST(HttpStatus.BAD_REQUEST, "요청 값이 올바르지 않습니다."),
    INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류가 발생했습니다.");

    private final HttpStatus status;
    private final String message;
}
