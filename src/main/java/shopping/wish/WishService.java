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
@Transactional
public class WishService {
    private final WishRepository wishRepository;
    private final ProductRepository productRepository;

    public WishService(WishRepository wishRepository, ProductRepository productRepository) {
        this.wishRepository = wishRepository;
        this.productRepository = productRepository;
    }

    public WishResponse add(Long memberId, WishRequest request) {
        Product product = findProduct(request.productId());
        validateNotWished(memberId, product.getId());
        Wish wish = wishRepository.save(new Wish(memberId, product.getId()));
        return WishResponse.of(wish, product);
    }

    @Transactional(readOnly = true)
    public List<WishResponse> findAll(Long memberId) {
        return wishRepository.findAllByMemberId(memberId).stream()
                .map(wish -> WishResponse.of(wish, findProduct(wish.getProductId())))
                .toList();
    }

    public void delete(Long memberId, Long wishId) {
        Wish wish = wishRepository.findById(wishId)
                .orElseThrow(() -> new NotFoundException("위시 리스트 항목을 찾을 수 없습니다."));
        if (!wish.isOwnedBy(memberId)) {
            throw new ForbiddenException("다른 회원의 위시 리스트 항목은 삭제할 수 없습니다.");
        }
        wishRepository.delete(wish);
    }

    private Product findProduct(Long productId) {
        if (productId == null) {
            throw new IllegalArgumentException("상품 ID는 필수입니다.");
        }
        return productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException("상품을 찾을 수 없습니다."));
    }

    private void validateNotWished(Long memberId, Long productId) {
        if (wishRepository.existsByMemberIdAndProductId(memberId, productId)) {
            throw new DuplicateException("이미 위시 리스트에 있는 상품입니다.");
        }
    }
}
