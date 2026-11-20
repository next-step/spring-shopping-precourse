package shopping.service;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import shopping.entity.Member;
import shopping.entity.Product;
import shopping.entity.Wish;
import shopping.exception.DuplicateWishException;
import shopping.repository.MemberRepository;
import shopping.repository.ProductRepository;
import shopping.repository.WishRepository;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional
@RequiredArgsConstructor
public class WishService {
    private final WishRepository wishRepository;
    private final MemberRepository memberRepository;
    private final ProductRepository productRepository;

    public Wish add(Long memberId, Long productId) {
        Member member = getMember(memberId);
        Product product = getProduct(productId);

        if (wishRepository.existsByMemberIdAndProductId(memberId, productId)) {
            throw new DuplicateWishException("Wish already exists");
        }

        Wish wish = Wish.of(member, product);
        try {
            return wishRepository.save(wish);
        } catch (DataIntegrityViolationException e) {
            throw new DuplicateWishException("Wish already exists", e);
        }
    }

    @Transactional(readOnly = true)
    public List<Wish> findAll(Long memberId) {
        Assert.notNull(memberId, "Member id cannot be null");
        return wishRepository.findAllByMemberId(memberId);
    }

    public void delete(Long memberId, Long wishId) {
        Assert.notNull(memberId, "Member id cannot be null");
        Member member = getMember(memberId);
        Wish wish = getWish(wishId);
        if (!wish.isOwnedBy(member)) {
            throw wishNotFound(wishId);
        }

        wishRepository.delete(wish);
    }

    private Member getMember(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new NoSuchElementException("Member not found. id = " + memberId));
    }

    private Product getProduct(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new NoSuchElementException("Product not found. id = " + productId));
    }

    private Wish getWish(Long wishId) {
        return wishRepository.findById(wishId)
                .orElseThrow(() -> wishNotFound(wishId));
    }

    private NoSuchElementException wishNotFound(Long wishId) {
        return new NoSuchElementException("Wish not found. id = " + wishId);
    }
}
