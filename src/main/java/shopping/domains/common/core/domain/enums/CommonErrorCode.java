package shopping.domains.common.core.domain.enums;

import lombok.Getter;

@Getter
public enum CommonErrorCode implements ErrorCode {
    INTERNAL_SERVER_ERROR("ERROR_00000001", "서버에서 에러가 발생했습니다."),
    DUPLICATE("ERROR_00000002", "중복된 요청입니다."),
    BAD_REQUEST("ERROR_00000003", "잘못된 요청입니다."),
    SERVICE_UNAVAILABLE("ERROR_00000004", "외부 요청에 실패했습니다."),
    UNAUTHORIZED("ERROR_00000005", "권한이 없습니다."),
    ALREADY_EXIST("ERROR_00000006", "이미 존재하는 데이터입니다."),
    NOT_EXIST("ERROR_00000007", "존재하지 않는 데이터입니다."),
    NOT_VALID("ERROR_00000008", "올바르지 않은 입력입니다."),
    ;

    private final String code;

    private final String message;

    CommonErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
