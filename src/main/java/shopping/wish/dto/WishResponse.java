package shopping.wish.dto;

import shopping.product.entity.Product;
import shopping.wish.entity.Wish;

public record WishResponse(Long id, Long productId, String name, int price, String imgUrl) {

    public static WishResponse of(Wish wish, Product product) {
        return new WishResponse(wish.getId(), product.getId(), product.getName(),
                product.getPrice(), product.getImgUrl());
    }
}
