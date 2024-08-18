package shopping.domains.user.core.out.adapter;

import lombok.NonNull;
import shopping.domains.user.core.domain.dto.WishlistDto;

import java.util.List;
import java.util.UUID;

public interface WishlistOutAdapter {
    @NonNull
    WishlistDto save(@NonNull final WishlistDto dto);

    boolean exist(
            @NonNull final UUID userId,
            @NonNull final UUID productId
    );

    void delete(@NonNull final UUID wishlistId);

    List<WishlistDto> findAll(@NonNull final UUID userId);
}
