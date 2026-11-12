package shopping.service;

import shopping.model.Member;
import shopping.model.Product;

public interface WishService {

    void addProductToWishList(Member member, Product product);

    boolean removeProductFromWishList(Member member, Long productId);

    Iterable<Product> getWishList(Member member);
}