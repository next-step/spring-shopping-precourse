package shopping.product;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ProductRequest(
        @NotNull
        @Size(min = 1, max = 15)
        @Pattern(regexp = "[\\p{L}\\p{N} ()\\[\\]+&/_-]+")
        String name,
        @NotNull Long price,
        @NotNull String imageUrl) {
}
