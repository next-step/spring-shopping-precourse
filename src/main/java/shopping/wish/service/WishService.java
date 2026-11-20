package shopping.wish.service;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shopping.common.exception.BusinessException;
import shopping.common.exception.ErrorCode;
import shopping.member.entity.Member;
import shopping.member.repository.MemberRepository;
import shopping.product.entity.Product;
import shopping.product.repository.ProductRepository;
import shopping.wish.dto.WishRequest;
import shopping.wish.dto.WishResponse;
import shopping.wish.entity.Wish;
import shopping.wish.repository.WishRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WishService {

    private final WishRepository wishRepository;
    private final MemberRepository memberRepository;
    private final ProductRepository productRepository;

    @Transactional
    public WishResponse add(Long memberId, WishRequest request) {
        Member member = findMember(memberId);

        Product product = productRepository.findById(request.productId())
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_EXIST_PRODUCT));

        if (wishRepository.existsByMemberAndProduct(member, product)) {
            throw new BusinessException(ErrorCode.DUPLICATE_WISH_APPLY);
        }

        Wish wish = saveWish(member, product);

        return WishResponse.from(wish);
    }

    @Transactional
    public void delete(Long memberId, Long wishId) {
        Member member = findMember(memberId);

        Wish wish = wishRepository.findByIdAndMember(wishId, member)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_EXIST_WISH));

        wishRepository.delete(wish);
    }

    @Transactional(readOnly = true)
    public List<WishResponse> findAll(Long memberId) {
        Member member = findMember(memberId);

        return wishRepository.findAllByMember(member).stream()
                .map(WishResponse::from)
                .toList();
    }

    private Member findMember(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new BusinessException(ErrorCode.INVALID_TOKEN));
    }

    private Wish saveWish(Member member, Product product) {
        try {
            return wishRepository.save(new Wish(member, product));
        } catch (DataIntegrityViolationException exception) {
            throw new BusinessException(ErrorCode.DUPLICATE_WISH_APPLY);
        }
    }
}
