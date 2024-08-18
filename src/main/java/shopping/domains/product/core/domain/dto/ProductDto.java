package shopping.domains.product.core.domain.dto;

import lombok.*;
import shopping.domains.common.core.domain.dto.BaseDto;
import shopping.domains.product.core.domain.entity.Product;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ProductDto extends BaseDto {
    private final UUID id;

    private final String name;

    private final Long price;

    private final String imageUrl;

    @Builder(toBuilder = true)
    public ProductDto(
            final UUID id,
            final LocalDateTime createdAt,
            final LocalDateTime updatedAt,
            final LocalDateTime deletedAt,
            final Long version,
            final String name,
            final Long price,
            final String imageUrl
    ) {
        super(createdAt, updatedAt, deletedAt, version);
        this.id = id;
        this.name = name;
        this.price = price;
        this.imageUrl = imageUrl;
    }

    @NonNull
    public ProductDto updateBase(@NonNull final ProductDto other) {
        return toBuilder()
                .id(other.id)
                .createdAt(other.createdAt)
                .updatedAt(other.updatedAt)
                .deletedAt(other.deletedAt)
                .build();
    }
}
