package shopping.wish.dto;

import jakarta.validation.constraints.NotNull;

public record WishRequest(
        @NotNull(message = "PRODUCT_ID_MISSING")
        Long productId
) {
}
