package shopping.wish;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shopping.common.ForbiddenException;
import shopping.common.NotFoundException;
import shopping.product.Product;
import shopping.product.ProductRepository;

@Service
@Transactional(readOnly = true)
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

    public List<WishResponse> findAll(Long memberId) {
        return wishRepository.findAllByMemberId(memberId).stream()
                .map(WishResponse::from)
                .toList();
    }

    @Transactional
    public void remove(Long memberId, Long wishId) {
        Wish wish = wishRepository.findById(wishId)
                .orElseThrow(() -> new NotFoundException("위시를 찾을 수 없습니다."));
        validateOwner(wish, memberId);
        wishRepository.delete(wish);
    }

    private Product getProduct(Long productId) {
        if (productId == null) {
            throw new IllegalArgumentException("상품 ID를 입력해야 합니다.");
        }
        return productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException("상품을 찾을 수 없습니다."));
    }

    private void validateNotWished(Long memberId, Long productId) {
        if (wishRepository.existsByMemberIdAndProductId(memberId, productId)) {
            throw new IllegalArgumentException("이미 위시 리스트에 담은 상품입니다.");
        }
    }

    private void validateOwner(Wish wish, Long memberId) {
        if (!wish.isOwnedBy(memberId)) {
            throw new ForbiddenException("다른 회원의 위시는 삭제할 수 없습니다.");
        }
    }
}
