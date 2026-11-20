package shopping.wish;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shopping.common.exception.ConflictException;
import shopping.common.exception.ForbiddenException;
import shopping.common.exception.NotFoundException;
import shopping.common.exception.UnauthorizedException;
import shopping.member.Member;
import shopping.member.MemberRepository;
import shopping.product.Product;
import shopping.product.ProductRepository;
import shopping.wish.dto.WishRequest;
import shopping.wish.dto.WishResponse;

@Service
@Transactional(readOnly = true)
public class WishService {

    private final WishRepository wishRepository;
    private final MemberRepository memberRepository;
    private final ProductRepository productRepository;

    public WishService(
            WishRepository wishRepository,
            MemberRepository memberRepository,
            ProductRepository productRepository) {
        this.wishRepository = wishRepository;
        this.memberRepository = memberRepository;
        this.productRepository = productRepository;
    }

    @Transactional
    public WishResponse add(Long memberId, WishRequest request) {
        Member member = getMember(memberId);
        Product product = getProduct(request.productId());
        validateNotDuplicated(memberId, product.getId());
        Wish wish = wishRepository.save(new Wish(member, product));
        return WishResponse.from(wish);
    }

    public List<WishResponse> findAll(Long memberId) {
        return wishRepository.findAllByMemberId(memberId).stream()
                .map(WishResponse::from)
                .toList();
    }

    @Transactional
    public void delete(Long memberId, Long wishId) {
        Wish wish = getWish(wishId);
        validateOwner(wish, memberId);
        wishRepository.delete(wish);
    }

    private Member getMember(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new UnauthorizedException("존재하지 않는 회원입니다."));
    }

    private Product getProduct(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException("상품을 찾을 수 없습니다. id=" + productId));
    }

    private Wish getWish(Long wishId) {
        return wishRepository.findById(wishId)
                .orElseThrow(() -> new NotFoundException("위시를 찾을 수 없습니다. id=" + wishId));
    }

    private void validateNotDuplicated(Long memberId, Long productId) {
        if (wishRepository.existsByMemberIdAndProductId(memberId, productId)) {
            throw new ConflictException("이미 위시 리스트에 있는 상품입니다.");
        }
    }

    private void validateOwner(Wish wish, Long memberId) {
        if (!wish.isOwnedBy(memberId)) {
            throw new ForbiddenException("다른 회원의 위시는 삭제할 수 없습니다.");
        }
    }
}
