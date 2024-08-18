package shopping.apps.shopping.api.user.response;

import lombok.NonNull;
import shopping.domains.product.core.domain.dto.ProductDto;
import shopping.domains.user.core.domain.dto.WishlistDto;
import shopping.domains.user.core.domain.entity.EncryptStrategy;

import java.util.Collection;
import java.util.List;

public record WishlistInfoResponses(
        @NonNull List<WishlistInfoResponse> data
) {
    public WishlistInfoResponses(
            @NonNull final Collection<WishlistDto> wishlistDtos,
            @NonNull final EncryptStrategy encryptStrategy
            ) {
        this(wishlistDtos.stream().map(dto -> new WishlistInfoResponse(dto ,encryptStrategy)).toList());
    }
}
