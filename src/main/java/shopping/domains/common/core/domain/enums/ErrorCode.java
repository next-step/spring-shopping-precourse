package shopping.domains.common.core.domain.enums;

import lombok.NonNull;

public interface ErrorCode {
    @NonNull
    String getCode();

    @NonNull
    String getMessage();
}
