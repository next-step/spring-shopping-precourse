package shopping.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record ProductRequest(
        @NotBlank String name,
        @NotNull @PositiveOrZero Integer price,
        @NotBlank String imageUrl
) {
}
