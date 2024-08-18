package shopping.domains.user.out.jpa.repository;

import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import shopping.domains.user.out.jpa.entity.JpaWishlist;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface WishlistRepository extends JpaRepository<JpaWishlist, UUID> {
    @NonNull
    Optional<JpaWishlist> findByUser_IdAndProduct_Id(
            @NonNull final UUID userId,
            @NonNull final UUID productId
    );

    boolean existsByUser_IdAndProduct_Id(
            @NonNull final UUID userId,
            @NonNull final UUID productId
    );

    List<JpaWishlist> findAllByUser_Id(@NonNull final UUID userId);
}
