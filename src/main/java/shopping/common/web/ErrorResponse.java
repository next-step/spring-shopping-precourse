package shopping.common.web;

import shopping.common.error.ErrorCode;

public record ErrorResponse(String code, String message) {

    public static ErrorResponse of(ErrorCode code) {
        return new ErrorResponse(code.name(), code.message());
    }
}
