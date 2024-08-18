package shopping.apps.shopping.api.user.response;

import lombok.NonNull;
import shopping.apps.shopping.api.product.response.ProductInfoResponse;
import shopping.domains.user.core.domain.dto.UserDto;
import shopping.domains.user.core.domain.entity.EncryptStrategy;

import java.time.LocalDateTime;
import java.util.UUID;

public record UserInfoResponse (
        UUID userId,

        String email,

        LocalDateTime createdAt,

        LocalDateTime updatedAt
) {
    public UserInfoResponse(
            @NonNull final UserDto userDto,
            @NonNull final EncryptStrategy encryptStrategy
            ) {
        this(
                userDto.getId(),
                encryptStrategy.decrypt(userDto.getEncryptedEmail()),
                userDto.getCreatedAt(),
                userDto.getUpdatedAt()
        );
    }
}
