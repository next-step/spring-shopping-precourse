package shopping.apps.shopping.api.user.response;

import lombok.NonNull;
import shopping.apps.shopping.api.product.response.ProductInfoResponse;
import shopping.domains.user.core.domain.dto.WishlistDto;
import shopping.domains.user.core.domain.entity.EncryptStrategy;

import java.time.LocalDateTime;
import java.util.UUID;

public record WishlistInfoResponse(
        UUID wishlistId,

        UserInfoResponse user,

        ProductInfoResponse product,

        LocalDateTime createdAt,

        LocalDateTime updatedAt
) {
    public WishlistInfoResponse(
            @NonNull final WishlistDto wishlistDto,
            @NonNull final EncryptStrategy encryptStrategy
    ) {
        this(
                wishlistDto.getId(),
                new UserInfoResponse(wishlistDto.getUser(), encryptStrategy),
                new ProductInfoResponse(wishlistDto.getProduct()),
                wishlistDto.getCreatedAt(),
                wishlistDto.getUpdatedAt()
        );
    }
}
