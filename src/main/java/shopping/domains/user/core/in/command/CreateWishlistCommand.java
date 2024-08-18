package shopping.domains.user.core.in.command;

import lombok.Builder;

import java.util.UUID;

public record CreateWishlistCommand (
        UUID userId,

        UUID productId
) {
    @Builder
    public CreateWishlistCommand {

    }
}
