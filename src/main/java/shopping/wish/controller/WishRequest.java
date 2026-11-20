package shopping.wish.controller;

import jakarta.validation.constraints.NotNull;

public record WishRequest(
        @NotNull(message = "상품 id는 필수입니다.")
        Long productId) {
}
