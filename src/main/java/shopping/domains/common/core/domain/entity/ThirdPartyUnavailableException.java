package shopping.domains.common.core.domain.entity;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import shopping.domains.common.core.domain.enums.ErrorCode;

@Getter
public class ThirdPartyUnavailableException extends RuntimeException {
    private final ErrorCode errorCode;

    public ThirdPartyUnavailableException(@NonNull final ErrorCode errorCode) {
        this.errorCode = errorCode;
    }
}