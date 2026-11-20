package shopping.wish;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import shopping.product.Product;
import shopping.product.ProductNotFoundException;
import shopping.product.ProductRepository;

@Service
public class WishService {
    private final WishRepository wishRepository;
    private final ProductRepository productRepository;

    public WishService(WishRepository wishRepository, ProductRepository productRepository) {
        this.wishRepository = wishRepository;
        this.productRepository = productRepository;
    }

    public WishResponse add(Long memberId, Long productId) {
        Product product = findProduct(productId);
        validateNotDuplicated(memberId, productId);
        Wish saved = wishRepository.save(new Wish(null, memberId, productId));
        return WishResponse.of(saved, product);
    }

    public List<WishResponse> getAll(Long memberId) {
        return wishRepository.findByMemberId(memberId).stream()
                .map(this::toResponse)
                .flatMap(Optional::stream)
                .toList();
    }

    public void remove(Long memberId, Long wishId) {
        Wish wish = wishRepository.findById(wishId)
                .filter(found -> found.isOwnedBy(memberId))
                .orElseThrow(() -> new WishNotFoundException(wishId));
        wishRepository.deleteById(wish.getId());
    }

    private Optional<WishResponse> toResponse(Wish wish) {
        return productRepository.findById(wish.getProductId())
                .map(product -> WishResponse.of(wish, product));
    }

    private Product findProduct(Long productId) {
        if (productId == null) {
            throw new IllegalArgumentException("상품 id는 필수입니다.");
        }
        return productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId));
    }

    private void validateNotDuplicated(Long memberId, Long productId) {
        if (wishRepository.existsByMemberIdAndProductId(memberId, productId)) {
            throw new DuplicateWishException();
        }
    }
}