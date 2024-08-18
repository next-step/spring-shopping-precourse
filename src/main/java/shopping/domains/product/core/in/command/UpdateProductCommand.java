package shopping.domains.product.core.in.command;

import lombok.Builder;
import lombok.NonNull;

import java.util.UUID;

public record UpdateProductCommand(
        @NonNull
        UUID productId,

        @NonNull String name,

        long price,

        String imageUrl
) {
    @Builder(toBuilder = true)
    public UpdateProductCommand {

    }
}
