package shopping.product.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record ProductRequest(
        @NotBlank(message = "이름은 필수 입력 항목입니다.")
        String name,

        @NotNull(message = "가격은 필수 입력 항목입니다.")
        @PositiveOrZero(message = "가격은 0 이상이어야 합니다.")
        Integer price,

        @NotNull(message = "재고는 필수 입력 항목입니다.")
        @PositiveOrZero(message = "재고는 0 이상이어야 합니다.")
        Integer stock,

        @NotBlank(message = "이미지 주소는 필수 입력 항목입니다.")
        String imgUrl
) {
}
