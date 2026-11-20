package shopping.wish.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shopping.common.exception.BusinessException;
import shopping.common.exception.ErrorCode;
import shopping.product.entity.Product;
import shopping.product.repository.ProductRepository;
import shopping.wish.dto.WishResponse;
import shopping.wish.entity.Wish;
import shopping.wish.repository.WishRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WishService {

    private final WishRepository wishRepository;
    private final ProductRepository productRepository;

    @Transactional
    public WishResponse addWish(Long memberId, Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));
        if (wishRepository.existsByMemberIdAndProductId(memberId, productId)) {
            throw new BusinessException(ErrorCode.DUPLICATE_WISH);
        }
        Wish wish = wishRepository.save(new Wish(memberId, productId));
        return WishResponse.of(wish, product);
    }

    public List<WishResponse> getWishes(Long memberId) {
        return wishRepository.findAllByMemberId(memberId).stream()
                .flatMap(wish -> productRepository.findById(wish.getProductId())
                        .map(product -> WishResponse.of(wish, product))
                        .stream())
                .toList();
    }

    @Transactional
    public void deleteWish(Long memberId, Long wishId) {
        Wish wish = wishRepository.findById(wishId)
                .orElseThrow(() -> new BusinessException(ErrorCode.WISH_NOT_FOUND));
        if (!wish.getMemberId().equals(memberId)) {
            throw new BusinessException(ErrorCode.WISH_FORBIDDEN);
        }
        wishRepository.delete(wish);
    }
}
