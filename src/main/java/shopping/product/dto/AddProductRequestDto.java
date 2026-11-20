package shopping.product.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public record AddProductRequestDto(
        @NotBlank(message = "상품 이름은 필수 입력값입니다.")
        @Size(max = 15, message = "상품 이름은 공백을 포함하여 최대 15자까지 입력할 수 있습니다.")
        String name,

        @NotNull(message = "가격은 필수 입력값입니다.")
        @Positive(message = "가격은 0보다 커야 합니다.")
        Integer price,

        @NotNull(message = "개수는 필수 입력값입니다.")
        @PositiveOrZero(message = "개수는 0 이상이어야 합니다.")
        Integer count,

        @NotBlank(message = "이미지 URL은 필수 입력값입니다.")
        String url
) { }
