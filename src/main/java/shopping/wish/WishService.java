package shopping.wish;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shopping.exception.ConflictException;
import shopping.exception.ForbiddenException;
import shopping.exception.NotFoundException;
import shopping.product.Product;
import shopping.product.ProductService;
import shopping.wish.dto.WishResponse;

@Service
public class WishService {

    private final WishRepository wishRepository;
    private final ProductService productService;

    public WishService(WishRepository wishRepository, ProductService productService) {
        this.wishRepository = wishRepository;
        this.productService = productService;
    }

    @Transactional
    public WishResponse add(Long memberId, Long productId) {
        Product product = productService.findById(productId);
        if (wishRepository.existsByMemberIdAndProductId(memberId, productId)) {
            throw new ConflictException("이미 위시 리스트에 담긴 상품입니다.");
        }
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
                .orElseThrow(() -> new NotFoundException("위시가 존재하지 않습니다."));
        if (!wish.isOwnedBy(memberId)) {
            throw new ForbiddenException("다른 회원의 위시는 삭제할 수 없습니다.");
        }
        wishRepository.delete(wish);
    }
}