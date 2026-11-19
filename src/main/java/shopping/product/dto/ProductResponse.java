package shopping.product.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import shopping.product.entity.Product;

public record ProductResponse(String name, int price, int stock, String imgUrl) {

    public static  ProductResponse from(Product product){
        return new ProductResponse(product.getName(), product.getPrice(), product.getStock(), product.getImgUrl());
    }

}
