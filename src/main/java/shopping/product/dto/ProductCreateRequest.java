package shopping.product.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record ProductCreateRequest(
        @NotBlank(message = "이름은 필수 입력 항목입니다.")
        String name,

        @NotNull
        @PositiveOrZero
        Integer price,

        @NotNull
        @PositiveOrZero
        Integer stock,

        @NotBlank(message = "이미지 주소는 필수 입력 항목입니다.")
        String imgUrl
) {}
