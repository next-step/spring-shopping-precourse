package shopping.apps.shopping.api.product.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.NonNull;
import shopping.apps.shopping.api.product.docs.ProductApiDocs;
import shopping.apps.shopping.api.user.docs.UserApiDocs;
import shopping.domains.product.core.domain.dto.ProductDto;
import shopping.domains.user.core.domain.dto.TokenDto;

import java.time.LocalDateTime;
import java.util.UUID;

public record ProductInfoResponse(
        UUID productId,

        @Schema(description = ProductApiDocs.Name.DESCRIPTION)
        String name,

        @Schema(description = ProductApiDocs.Price.DESCRIPTION)
        Long price,

        @Schema(example = ProductApiDocs.Image.DOWNLOAD_URL_EXAMPLE, description = ProductApiDocs.Image.DOWNLOAD_URL_DESCRIPTION)
        String imageDownloadUrl,

        LocalDateTime createdAt,

        LocalDateTime updatedAt
) {
    public ProductInfoResponse(@NonNull final ProductDto productDto) {
        this(
                productDto.getId(),
                productDto.getName(),
                productDto.getPrice(),
                productDto.getImageUrl(),
                productDto.getCreatedAt(),
                productDto.getUpdatedAt()
        );
    }
}
