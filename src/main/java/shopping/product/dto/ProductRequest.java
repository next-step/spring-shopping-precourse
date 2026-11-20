package shopping.product.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;

public record ProductRequest(
        @NotBlank(message = "PRODUCT_NAME_MISSING")
        @Size(max = 15, message = "PRODUCT_NAME_TOO_LONG")
        @Pattern(regexp = "^[가-힣a-zA-Z0-9 ()\\[\\]+\\-&/_]*$", message = "NOT_ALLOWED_CHARACTERS")
        String name,

        @NotNull(message = "PRICE_MISSING")
        @Min(value = 0, message = "EXCEED_PRICE")
        @Max(value = 2_147_483_647, message = "EXCEED_PRICE")
        Long price,

        @NotBlank(message = "URL_MISSING")
        @Pattern(regexp = "^https?://\\S+$", message = "INVALID_URL_FORMAT")
        String url
) {
}
