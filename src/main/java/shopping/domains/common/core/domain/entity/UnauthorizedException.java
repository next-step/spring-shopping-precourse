package shopping.domains.common.core.domain.entity;

import lombok.Getter;
import lombok.NonNull;
import shopping.domains.common.core.domain.enums.ErrorCode;

@Getter
public class UnauthorizedException extends RuntimeException {
    private final ErrorCode errorCode;

    public UnauthorizedException(@NonNull final ErrorCode errorCode) {
        this.errorCode = errorCode;
    }
}
