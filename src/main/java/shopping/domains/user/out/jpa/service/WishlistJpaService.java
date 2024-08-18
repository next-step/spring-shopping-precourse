package shopping.domains.user.out.jpa.service;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import shopping.domains.user.core.domain.dto.WishlistDto;
import shopping.domains.user.core.out.adapter.WishlistOutAdapter;
import shopping.domains.user.out.jpa.entity.JpaWishlist;
import shopping.domains.user.out.jpa.repository.WishlistRepository;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class WishlistJpaService implements WishlistOutAdapter {
    private final WishlistRepository wishlistRepository;

    @Override
    public @NonNull WishlistDto save(@NonNull final WishlistDto dto) {
        return wishlistRepository.save(new JpaWishlist(dto)).toDto();
    }

    @Override
    public boolean exist(
            @NonNull final UUID userId,
            @NonNull final UUID productId
    ) {
        return wishlistRepository.existsByUser_IdAndProduct_Id(userId, productId);
    }

    @Override
    public void delete(@NonNull final UUID wishlistId) {
        wishlistRepository.deleteById(wishlistId);
    }

    @Override
    public List<WishlistDto> findAll(@NonNull final UUID userId) {
        return wishlistRepository.findAllByUser_Id(userId)
                .stream()
                .map(JpaWishlist::toDto)
                .toList();
    }
}
