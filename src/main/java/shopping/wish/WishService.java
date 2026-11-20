package shopping.wish;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import shopping.member.Member;
import shopping.member.MemberRepository;
import shopping.product.Product;
import shopping.product.ProductRepository;

@Service
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
    public void delete(Long memberId, Long wishId) {
        findMember(memberId);
        Wish wish = wishRepository.findByIdAndMemberId(wishId, memberId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "위시리스트 항목을 찾을 수 없습니다"));
        wishRepository.delete(wish);
    }

    @Transactional(readOnly = true)
    public List<WishResponse> findAll(Long memberId) {
        findMember(memberId);
        return wishRepository.findAllByMemberId(memberId).stream()
                .map(WishResponse::from)
                .toList();
    }

    @Transactional
    public Long add(Long memberId, Long productId) {
        Wish wish = new Wish(findMember(memberId), findProduct(productId));
        return wishRepository.save(wish).getId();
    }

    private Member findMember(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "회원을 찾을 수 없습니다"));
    }

    private Product findProduct(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "상품을 찾을 수 없습니다"));
    }
}
