package shopping.apps.shopping.api.user.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import lombok.NonNull;
import shopping.apps.shopping.api.product.docs.ProductApiDocs;
import shopping.domains.product.core.domain.entity.Image;
import shopping.domains.product.core.domain.entity.Name;
import shopping.domains.product.core.in.command.CreateProductCommand;
import shopping.domains.product.core.in.command.UpdateProductCommand;
import shopping.domains.user.core.in.command.CreateWishlistCommand;

import java.util.UUID;

public record WriteWishlistRequest(

        UUID productId
) {
    public CreateWishlistCommand toCreateCommand(@NonNull final UUID userId) {
        return CreateWishlistCommand.builder()
                .userId(userId)
                .productId(productId)
                .build();
    }
}
