package shopping.wish.application;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shopping.common.error.ErrorCode;
import shopping.common.error.ShoppingException;
import shopping.product.domain.Product;
import shopping.product.persistence.ProductRepository;
import shopping.wish.domain.Wish;
import shopping.wish.persistence.WishRepository;

@Service
@Transactional(readOnly = true)
public class WishService {

    private final WishRepository wishes;
    private final ProductRepository products;

    public WishService(WishRepository wishes, ProductRepository products) {
        this.wishes = wishes;
        this.products = products;
    }

    @Transactional
    public Wish add(Long memberId, Long productId) {
        Product product = products.findById(productId)
                .orElseThrow(() -> new ShoppingException(ErrorCode.PRODUCT_NOT_FOUND));
        rejectDuplicate(memberId, productId);

        return wishes.saveAndFlush(new Wish(memberId, product));
    }

    public List<Wish> list(Long memberId) {
        return wishes.findAllWithProducts(memberId);
    }

    @Transactional
    public void delete(Long memberId, Long wishId) {
        Wish wish = wishes.findById(wishId)
                .orElseThrow(() -> new ShoppingException(ErrorCode.WISH_NOT_FOUND));
        wish.requireOwner(memberId);

        wishes.delete(wish);
    }

    private void rejectDuplicate(Long memberId, Long productId) {
        if (wishes.existsByMemberIdAndProduct_Id(memberId, productId)) {
            throw new ShoppingException(ErrorCode.WISH_ALREADY_EXISTS);
        }
    }
}
