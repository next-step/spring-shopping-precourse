package shopping.wish;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shopping.common.DuplicateException;
import shopping.common.ForbiddenException;
import shopping.common.NotFoundException;
import shopping.product.Product;
import shopping.product.ProductRepository;

@Service
public class WishService {

    private final WishRepository wishRepository;
    private final ProductRepository productRepository;

    public WishService(WishRepository wishRepository, ProductRepository productRepository) {
        this.wishRepository = wishRepository;
        this.productRepository = productRepository;
    }

    @Transactional
    public WishResponse add(Long memberId, WishRequest request) {
        Product product = getProduct(request.productId());
        validateNotWished(memberId, product.getId());
        Wish wish = wishRepository.save(new Wish(memberId, product));
        return WishResponse.from(wish);
    }

    @Transactional(readOnly = true)
    public List<WishResponse> findAll(Long memberId) {
        return wishRepository.findAllByMemberId(memberId).stream()
                .map(WishResponse::from)
                .toList();
    }

    @Transactional
    public void delete(Long memberId, Long wishId) {
        Wish wish = wishRepository.findById(wishId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 위시 리스트 항목입니다."));
        validateOwner(wish, memberId);
        wishRepository.delete(wish);
    }

    private Product getProduct(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 상품입니다."));
    }

    private void validateNotWished(Long memberId, Long productId) {
        if (wishRepository.existsByMemberIdAndProductId(memberId, productId)) {
            throw new DuplicateException("이미 위시 리스트에 추가한 상품입니다.");
        }
    }

    private void validateOwner(Wish wish, Long memberId) {
        if (!wish.isOwnedBy(memberId)) {
            throw new ForbiddenException("다른 사용자의 위시 리스트 항목은 삭제할 수 없습니다.");
        }
    }
}
