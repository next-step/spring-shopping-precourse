package shopping.wish.web;

import jakarta.validation.constraints.NotNull;

public record WishRequest(@NotNull Long productId) {
}
