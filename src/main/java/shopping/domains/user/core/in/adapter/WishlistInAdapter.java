package shopping.domains.user.core.in.adapter;

import lombok.NonNull;
import shopping.domains.user.core.in.command.CreateWishlistCommand;
import shopping.domains.user.core.domain.dto.WishlistDto;

import java.util.List;
import java.util.UUID;

public interface WishlistInAdapter {
    @NonNull
    WishlistDto createWishlist(@NonNull final CreateWishlistCommand command);

    void deleteWishlist(@NonNull final UUID wishlistId);

    @NonNull List<WishlistDto> getAllWishlist(@NonNull final UUID userId);
}
