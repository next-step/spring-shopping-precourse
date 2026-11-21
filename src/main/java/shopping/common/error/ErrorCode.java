package shopping.common.error;

public enum ErrorCode {
    INVALID_CREDENTIALS("이메일 또는 비밀번호가 올바르지 않습니다."),
    INVALID_TOKEN("유효한 인증 토큰이 필요합니다."),
    PROFANITY_CHECK_UNAVAILABLE("비속어 검사를 수행할 수 없습니다."),
    PROFANITY_DETECTED("상품 이름에 비속어를 사용할 수 없습니다."),
    PRODUCT_NOT_FOUND("상품을 찾을 수 없습니다."),
    WISH_ALREADY_EXISTS("이미 위시리스트에 등록된 상품입니다."),
    FORBIDDEN("접근 권한이 없습니다."),
    WISH_NOT_FOUND("위시리스트 항목을 찾을 수 없습니다."),
    INVALID_REQUEST("요청 값이 올바르지 않습니다."),
    ROUTE_NOT_FOUND("요청 경로를 찾을 수 없습니다."),
    METHOD_NOT_ALLOWED("지원하지 않는 HTTP 메서드입니다."),
    NOT_ACCEPTABLE("지원하지 않는 응답 형식입니다."),
    UNSUPPORTED_MEDIA_TYPE("지원하지 않는 본문 형식입니다."),
    EMAIL_ALREADY_EXISTS("이미 가입된 이메일입니다."),
    INTERNAL_SERVER_ERROR("서버 내부 오류가 발생했습니다.");

    private final String message;

    ErrorCode(String message) {
        this.message = message;
    }

    public String message() {
        return message;
    }
}
