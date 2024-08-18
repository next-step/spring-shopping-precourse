package shopping.domains.user.out.jpa.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import shopping.domains.common.out.jpa.entity.BaseJpaEntity;
import shopping.domains.product.out.jpa.entity.JpaProduct;
import shopping.domains.user.core.domain.dto.WishlistDto;
import shopping.domains.user.core.domain.entity.Wishlist;

import java.util.UUID;

@Entity
@Table(name = "wishlists")
@Getter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class JpaWishlist extends BaseJpaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "wishlist_id")
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private JpaUser user;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private JpaProduct product;

    @Embedded
    @Getter(value = AccessLevel.NONE)
    private Wishlist wishlist;

    public JpaWishlist(@NonNull final WishlistDto dto) {
        super(dto);

        this.id = dto.getId();
        this.user = new JpaUser(dto.getUser());
        this.product = new JpaProduct(dto.getProduct());
        this.wishlist = dto.getDeletedAt() == null ? new Wishlist(dto) : null;
    }

    @NonNull
    public WishlistDto toDto() {
        if (wishlist == null) {
            return WishlistDto.builder()
                    .id(id)
                    .version(version)
                    .createdAt(createdAt)
                    .updatedAt(updatedAt)
                    .deletedAt(deletedAt)
                    .build();
        }
        return wishlist.toDto()
                .toBuilder()
                .id(id)
                .user(user.toDto())
                .product(product.toDto())
                .version(version)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .deletedAt(deletedAt)
                .build();
    }

//    @PreUpdate
//    private void fetchStateBefore() {
//        handleSoftDelete();
//    }

    @PrePersist
    @PreUpdate
    private void fetchStateAfter() {
        if (handleSoftDelete()) {
            return;
        }
        if (user.getUser().isEmpty() || product.getProduct().isEmpty()) {
            return;
        }

        this.wishlist = Wishlist.builder()
                .user(user.getUser().get())
                .product(product.getProduct().get())
                .build();
    }

    private boolean handleSoftDelete() {
        if (deletedAt != null) {
            this.wishlist = null;
            return true;
        }
        return false;
    }
}

