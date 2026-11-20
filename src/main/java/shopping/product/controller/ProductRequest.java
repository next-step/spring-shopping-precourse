package shopping.product.controller;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public record ProductRequest(
        String name,
        @PositiveOrZero(message = "상품 가격은 0 이상이어야 합니다.")
        int price,
        @NotBlank(message = "상품 이미지 URL은 비어 있을 수 없습니다.")
        @Size(max = 2048, message = "상품 이미지 URL은 2048자 이하여야 합니다.")
        @Pattern(
                regexp = "^https?://.+",
                message = "상품 이미지 URL은 https:// 또는 https://로 시작해야 합니다.")
        String imageUrl) {

}
