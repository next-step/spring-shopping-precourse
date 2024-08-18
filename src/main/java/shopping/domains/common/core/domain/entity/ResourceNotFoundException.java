package shopping.domains.common.core.domain.entity;

import lombok.Getter;
import lombok.NonNull;
import shopping.domains.common.core.domain.enums.ErrorCode;

@Getter
public class ResourceNotFoundException extends RuntimeException {
    private final ErrorCode errorCode;

    public ResourceNotFoundException(@NonNull final ErrorCode errorCode) {
        this.errorCode = errorCode;
    }
}
