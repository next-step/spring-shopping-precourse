package shopping.domains.user.core.domain.dto;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import shopping.domains.common.core.domain.dto.BaseDto;
import shopping.domains.product.core.domain.dto.ProductDto;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@EqualsAndHashCode(callSuper = true)
@ToString
public class WishlistDto extends BaseDto {
    private final UUID id;

    private final UserDto user;

    private final ProductDto product;

    @Builder(toBuilder = true)
    public WishlistDto(
            final UUID id,
            final LocalDateTime createdAt,
            final LocalDateTime updatedAt,
            final LocalDateTime deletedAt,
            final Long version,
            final UserDto user,
            final ProductDto product
    ) {
        super(createdAt, updatedAt, deletedAt, version);
        this.id = id;
        this.user = user;
        this.product = product;
    }
}
