package shopping.wish;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shopping.common.DuplicateException;
import shopping.common.ForbiddenException;
import shopping.common.NotFoundException;
import shopping.member.Member;
import shopping.product.Product;
import shopping.product.ProductRepository;
import shopping.wish.dto.WishRequest;
import shopping.wish.dto.WishResponse;

@Service
public class WishService {

    private final WishRepository wishRepository;
    private final ProductRepository productRepository;

    public WishService(WishRepository wishRepository, ProductRepository productRepository) {
        this.wishRepository = wishRepository;
        this.productRepository = productRepository;
    }

    @Transactional
    public WishResponse add(Member member, WishRequest request) {
        Product product = productRepository.findById(request.productId())
                .orElseThrow(() -> new NotFoundException("존재하지 않는 상품입니다."));
        checkDuplicate(member, product);
        Wish wish = wishRepository.save(new Wish(member, product));
        return WishResponse.from(wish);
    }

    public List<WishResponse> findAll(Member member) {
        return wishRepository.findByMember(member).stream()
                .map(WishResponse::from)
                .toList();
    }

    @Transactional
    public void delete(Member member, Long wishId) {
        Wish wish = wishRepository.findById(wishId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 위시입니다."));
        checkOwner(member, wish);
        wishRepository.delete(wish);
    }

    private void checkDuplicate(Member member, Product product) {
        if (wishRepository.existsByMemberAndProduct(member, product)) {
            throw new DuplicateException("이미 위시에 담긴 상품입니다.");
        }
    }

    private void checkOwner(Member member, Wish wish) {
        if (!wish.getMember().getId().equals(member.getId())) {
            throw new ForbiddenException("본인의 위시만 삭제할 수 있습니다.");
        }
    }
}