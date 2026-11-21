package shopping.wish.application;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shopping.auth.exception.InvalidTokenException;
import shopping.member.domain.Member;
import shopping.member.domain.MemberRepository;
import shopping.product.domain.Product;
import shopping.product.domain.ProductRepository;
import shopping.product.exception.ProductNotFoundException;
import shopping.wish.domain.Wish;
import shopping.wish.domain.WishRepository;
import shopping.wish.dto.WishResponse;
import shopping.wish.exception.DuplicateWishException;
import shopping.wish.exception.ForbiddenWishException;
import shopping.wish.exception.WishNotFoundException;

@Service
public class WishService {

    private final WishRepository wishRepository;
    private final MemberRepository memberRepository;
    private final ProductRepository productRepository;

    public WishService(WishRepository wishRepository, MemberRepository memberRepository,
            ProductRepository productRepository) {
        this.wishRepository = wishRepository;
        this.memberRepository = memberRepository;
        this.productRepository = productRepository;
    }

    @Transactional
    public WishResponse add(Long memberId, Long productId) {
        Member member = findMember(memberId);
        Product product = findProduct(productId);
        validateDuplicate(memberId, productId);
        return WishResponse.from(wishRepository.save(new Wish(member, product)));
    }

    @Transactional(readOnly = true)
    public List<WishResponse> findAll(Long memberId) {
        return wishRepository.findAllByMember_Id(memberId).stream()
                .map(WishResponse::from)
                .toList();
    }

    @Transactional
    public void delete(Long memberId, Long wishId) {
        Wish wish = wishRepository.findById(wishId).orElseThrow(WishNotFoundException::new);
        validateOwner(wish, memberId);
        wishRepository.delete(wish);
    }

    private Member findMember(Long memberId) {
        return memberRepository.findById(memberId).orElseThrow(InvalidTokenException::new);
    }

    private Product findProduct(Long productId) {
        return productRepository.findById(productId).orElseThrow(ProductNotFoundException::new);
    }

    private void validateDuplicate(Long memberId, Long productId) {
        if (wishRepository.existsByMember_IdAndProduct_Id(memberId, productId)) {
            throw new DuplicateWishException();
        }
    }

    private void validateOwner(Wish wish, Long memberId) {
        if (!wish.isOwnedBy(memberId)) {
            throw new ForbiddenWishException();
        }
    }
}
