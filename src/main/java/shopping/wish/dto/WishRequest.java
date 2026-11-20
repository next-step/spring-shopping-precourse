package shopping.wish.dto;

import jakarta.validation.constraints.NotNull;

public record WishRequest(
        @NotNull(message = "상품 id는 필수 입력 항목입니다.")
        Long productId
) {
}
