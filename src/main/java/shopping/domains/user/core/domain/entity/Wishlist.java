package shopping.domains.user.core.domain.entity;

import jakarta.persistence.Embeddable;
import jakarta.persistence.Transient;
import lombok.*;
import shopping.domains.product.core.domain.entity.Product;
import shopping.domains.user.core.domain.dto.UserDto;
import shopping.domains.user.core.domain.dto.WishlistDto;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode
@ToString
public class Wishlist {
    @Transient
    private User user;

    @Transient
    private Product product;

    @Builder(toBuilder = true)
    public Wishlist(
            @NonNull final User user,
            @NonNull final Product product
    ) {
        this.user = user;
        this.product = product;
    }

    public Wishlist(@NonNull final WishlistDto dto) {
        this(
                new User(dto.getUser()),
                new Product(dto.getProduct())
        );
    }

    @NonNull
    public WishlistDto toDto() {
        return WishlistDto.builder()
                .user(user.toDto())
                .product(product.toDto())
                .build();
    }
}
