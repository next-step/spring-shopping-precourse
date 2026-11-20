package shopping.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class CreateProductRequest {
    @NotBlank(message = "상품명을 입력해 주세요.")
    @Pattern(
            regexp = "[\\p{L}\\p{Nd} ()\\[\\]+&/_-]{0,15}",
            message = "상품명은 공백 포함 15자 이내여야 하며 허용된 문자만 사용할 수 있습니다."
    )
    private String productName;
    private int price;
    private String imageUrl;

    public CreateProductRequest(String productName, int price, String imageUrl) {
        this.productName = productName;
        this.price = price;
        this.imageUrl = imageUrl;
    }

    public String getProductName() {
        return productName;
    }

    public int getPrice() {
        return price;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}
