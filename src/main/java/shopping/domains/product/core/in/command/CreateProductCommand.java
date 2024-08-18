package shopping.domains.product.core.in.command;

import lombok.Builder;
import lombok.NonNull;

public record CreateProductCommand(
        @NonNull String name,

        long price,

        String imageUrl
) {
    @Builder(toBuilder = true)
    public CreateProductCommand {

    }
}
