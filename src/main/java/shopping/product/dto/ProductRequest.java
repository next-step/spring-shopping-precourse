package shopping.product.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public record ProductRequest(
        @NotBlank(message = "상품 이름은 비어 있을 수 없습니다.")
        @Size(max = 15, message = "상품 이름은 15자를 초과할 수 없습니다.")
        @Pattern(
                regexp = "^[가-힣A-Za-z0-9\\s()\\[\\]+&/_-]*$",
                message = "상품 이름에 허용되지 않은 특수 문자가 포함되어 있습니다."
        )
        String name,

        @NotNull(message = "상품 가격은 비어 있을 수 없습니다.")
        @PositiveOrZero(message = "상품 가격은 음수일 수 없습니다.")
        Long price,

        @NotBlank(message = "이미지 URL은 비어 있을 수 없습니다.")
        String imageUrl
) {
}
