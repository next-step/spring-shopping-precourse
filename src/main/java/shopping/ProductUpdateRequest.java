package shopping;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.URL;

public record ProductUpdateRequest(
        @NotBlank(message = "상품 이름은 필수입니다.")
                @Size(max = 15, message = "상품 이름은 최대 15자입니다.")
                @Pattern(
                        regexp = "^[\\p{L}\\p{M}\\p{N} ()\\[\\]+&/_-]+$",
                        message = "상품 이름에 허용되지 않는 문자가 있습니다.")
                String name,
        @NotNull(message = "가격은 필수입니다.") @PositiveOrZero(message = "가격은 0원 이상이어야 합니다.")
                Integer price,
        @NotBlank(message = "이미지 URL은 필수 입니다.")
                @URL(regexp = "(?i)^https?://.*$", message = "이미지 URL은 http 또는 https 형식이어야 합니다.")
                String imageUrl) {
    public ProductUpdateRequest {
        if (name != null) {
            name = name.strip();
        }
    }
}
