package shopping.domains.product.core.domain.entity;

import jakarta.persistence.Embeddable;
import lombok.*;
import shopping.domains.product.core.domain.dto.ProductDto;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode
@ToString
public class Product {
    private Name name;

    private Price price;

    private Image image;

    @Builder(toBuilder = true)
    public Product(
            @NonNull final Name name,
            @NonNull final Price price,
            @NonNull final Image image
    ) {
        this.name = name;
        this.price = price;
        this.image = image;
    }

    public Product(@NonNull final ProductDto dto) {
        this(
                new Name(dto.getName()),
                new Price(dto.getPrice()),
                new Image(dto.getImageUrl())
        );
    }

    @NonNull
    public ProductDto toDto() {
        return ProductDto.builder()
                .name(name.getValue())
                .price(price.getValue())
                .imageUrl(image.getDownloadUrl())
                .build();
    }
}
