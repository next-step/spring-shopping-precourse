package shopping.wish.service;

import java.util.List;
import org.springframework.stereotype.Service;
import shopping.product.domain.Product;
import shopping.product.exception.ProductNotFoundException;
import shopping.product.repository.ProductRepository;
import shopping.wish.domain.Wish;
import shopping.wish.exception.DuplicateWishException;
import shopping.wish.exception.WishAccessDeniedException;
import shopping.wish.exception.WishNotFoundException;
import shopping.wish.repository.WishRepository;


@Service
public class WishService {
    private final WishRepository wishRepository;
    private final ProductRepository productRepository;

    public WishService(WishRepository wishRepository, ProductRepository productRepository) {
        this.wishRepository = wishRepository;
        this.productRepository = productRepository;
    }

    public Wish add(Long memberId, Long productId) {
        Product product = findProduct(productId);
        validateNotDuplicated(memberId, productId);
        return wishRepository.save(new Wish(null, memberId, product));
    }

    public List<Wish> findAll(Long memberId) {
        return wishRepository.findAllByMemberId(memberId);
    }

    public void delete(Long memberId, Long wishId) {
        Wish wish = wishRepository.findById(wishId)
                .orElseThrow(() -> new WishNotFoundException("위시를 찾을 수 없습니다."));
        validateOwner(wish, memberId);
        wishRepository.delete(wish);
    }

    private Product findProduct(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("상품을 찾을 수 없습니다."));
    }

    private void validateNotDuplicated(Long memberId, Long productId) {
        if (wishRepository.existsByMemberIdAndProductId(memberId, productId)) {
            throw new DuplicateWishException("이미 위시리스트에 담은 상품입니다.");
        }
    }

    private void validateOwner(Wish wish, Long memberId) {
        if (!wish.isOwnedBy(memberId)) {
            throw new WishAccessDeniedException("본인의 위시만 삭제할 수 있습니다.");
        }
    }
}
