package shopping.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import shopping.model.ProductName;

public record ProductRequest(
        @NotNull
        @Size(min = ProductName.MIN_LENGTH, max = ProductName.MAX_LENGTH)
        @Pattern(regexp = ProductName.PATTERN)
        String name,
        @NotNull Long price,
        @NotNull String imageUrl) {
}
