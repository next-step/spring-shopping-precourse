package shopping.controller;

import jakarta.validation.constraints.NotNull;

public class ProductWishRequest {

    @NotNull
    private Long productId;

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }
}
