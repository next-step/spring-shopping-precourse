package shopping.domains.user.core.domain.service;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import shopping.domains.common.core.domain.entity.AlreadyExistResourceException;
import shopping.domains.common.core.domain.entity.ResourceNotFoundException;
import shopping.domains.common.core.domain.enums.CommonErrorCode;
import shopping.domains.product.core.domain.dto.ProductDto;
import shopping.domains.product.core.out.adapter.ProductOutAdapter;
import shopping.domains.user.core.in.command.CreateWishlistCommand;
import shopping.domains.user.core.domain.dto.UserDto;
import shopping.domains.user.core.domain.dto.WishlistDto;
import shopping.domains.user.core.in.adapter.WishlistInAdapter;
import shopping.domains.user.core.out.adapter.UserOutAdapter;
import shopping.domains.user.core.out.adapter.WishlistOutAdapter;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class WishlistService implements WishlistInAdapter {
    private final UserOutAdapter userOutAdapter;

    private final ProductOutAdapter productOutAdapter;

    private final WishlistOutAdapter wishlistOutAdapter;

    @Override
    public @NonNull WishlistDto createWishlist(@NonNull final CreateWishlistCommand command) {
        if(wishlistOutAdapter.exist(command.userId(), command.productId())) {
            throw new AlreadyExistResourceException(CommonErrorCode.ALREADY_EXIST);
        }

        final UserDto userDto = userOutAdapter.findUser(command.userId())
                .orElseThrow(() -> new ResourceNotFoundException(CommonErrorCode.NOT_EXIST));
        final ProductDto productDto = productOutAdapter.findProduct(command.productId())
                .orElseThrow(() -> new ResourceNotFoundException(CommonErrorCode.NOT_EXIST));
        final WishlistDto wishlistDto = WishlistDto.builder()
                .user(userDto)
                .product(productDto)
                .build();
        return wishlistOutAdapter.save(wishlistDto);
    }

    @Override
    public void deleteWishlist(@NonNull final UUID wishlistId) {
        wishlistOutAdapter.delete(wishlistId);
    }

    @Override
    public @NonNull List<WishlistDto> getAllWishlist(@NonNull final UUID userId) {
        return wishlistOutAdapter.findAll(userId);
    }
}
