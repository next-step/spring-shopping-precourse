package shopping.product.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public record ProductRequest(
    @NotBlank(message = "상품 이름은 비어 있을 수 없습니다.")
    @Size(max = 15, message = "상품 이름은 공백포함 15자를 넘을 수 없습니다.")
    @Pattern(regexp = "^[가-힣a-zA-Z0-9 ()\\[\\]+\\-&/_]*$",
    message = "상품 이름에 허용되지 않은 문자가 있습니다.")
    @Pattern(regexp = "^(?!.* {2}).*$", message =  "상품 이름에 공백을 연속으로 쓸 수 없습니다.")
    String name,

    @NotNull(message = "가격은 픨수입니다.")
    @PositiveOrZero(message =  "가격은 0이상이여야합니다.")
    int price,

    @NotBlank(message = "이미지 URL은 비어 있을 수 없습니다.")
    String imageUrl
            ){

}
