package shopping.domains.common.core.domain.entity;

import lombok.Getter;
import lombok.NonNull;
import shopping.domains.common.core.domain.enums.ErrorCode;

@Getter
public class ClientIllegalArgumentException extends IllegalArgumentException {
    private final ErrorCode errorCode;

    public ClientIllegalArgumentException(@NonNull final ErrorCode errorCode) {
        this.errorCode = errorCode;
    }

}
