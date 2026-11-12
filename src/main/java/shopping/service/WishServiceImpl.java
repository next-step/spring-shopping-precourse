package shopping.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import shopping.model.Member;
import shopping.model.Product;
import shopping.model.Wish;
import shopping.repository.WishRepository;

@Service
public class WishServiceImpl implements WishService {

    private final WishRepository wishRepository;

    public WishServiceImpl(WishRepository wishRepository) {
        this.wishRepository = wishRepository;
    }

    @Override
    public void addProductToWishList(Member member, Product product) {
        // 위시 리스트에 상품 추가
        Wish wish = new Wish(member, product);
        wishRepository.save(wish);
    }

    @Override
    public boolean removeProductFromWishList(Member member, Long productId) {
        // 위시 리스트에서 상품 삭제
        return wishRepository.deleteByMemberAndProductId(member, productId) > 0;
    }

    @Override
    public Iterable<Product> getWishList(Member member) {
        // 사용자의 위시 리스트에서 상품 목록 조회
        return wishRepository.findProductsByMember(member);
    }
}
