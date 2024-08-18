package shopping.apps.shopping.api.product.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import lombok.NonNull;
import shopping.apps.shopping.api.product.docs.ProductApiDocs;
import shopping.domains.product.core.domain.entity.Image;
import shopping.domains.product.core.domain.entity.Name;
import shopping.domains.product.core.in.command.CreateProductCommand;
import shopping.domains.product.core.in.command.UpdateProductCommand;

import java.util.UUID;

public record WriteProductRequest(
        @Schema(example = ProductApiDocs.Name.EXAMPLE, description = ProductApiDocs.Name.DESCRIPTION)
        @Pattern(regexp = Name.REG_EXP, message = ProductApiDocs.Name.REG_EXP_ERROR_MESSAGE)
        String name,

        @Schema(description = ProductApiDocs.Price.DESCRIPTION)
        long price,

        @Schema(example = ProductApiDocs.Image.DOWNLOAD_URL_EXAMPLE, description = ProductApiDocs.Image.DOWNLOAD_URL_DESCRIPTION)
        @Pattern(regexp = Image.REG_EXP, message =  ProductApiDocs.Image.DOWNLOAD_URL_REG_EXP_ERROR_MESSAGE)
        String imageDownloadUrl
) {
    public CreateProductCommand toCreateCommand() {
        return CreateProductCommand.builder()
                .name(name)
                .price(price)
                .imageUrl(imageDownloadUrl)
                .build();
    }

    public UpdateProductCommand toUpdateCommand(@NonNull final UUID productId) {
        return UpdateProductCommand.builder()
                .productId(productId)
                .name(name)
                .price(price)
                .imageUrl(imageDownloadUrl)
                .build();
    }
}
