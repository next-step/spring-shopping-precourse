package shopping.wish.application;

import static shopping.shared.application.RequestFailure.Reason.CONFLICT;
import static shopping.shared.application.RequestFailure.Reason.INVALID_INPUT;
import static shopping.shared.application.RequestFailure.Reason.NOT_FOUND;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shopping.product.application.ProductRepository;
import shopping.shared.application.RequestFailure;
import shopping.wish.application.WishRepository.ListedWish;
import shopping.wish.domain.Wish;

@Service
public class WishService {
    private final WishRepository wishes;
    private final ProductRepository products;

    public WishService(WishRepository wishes, ProductRepository products) {
        this.wishes = wishes;
        this.products = products;
    }

    public List<ListedWish> list(long memberId) {
        return wishes.findByMemberId(memberId);
    }

    @Transactional
    public long add(long memberId, Long productId) {
        if (productId == null || productId <= 0) {
            throw new RequestFailure(INVALID_INPUT, "유효한 상품 ID를 입력해 주세요.");
        }
        return wishes.add(new Wish(memberId, productId)).orElseThrow(() -> addFailure(productId));
    }

    public void delete(long memberId, long wishId) {
        if (!wishes.delete(memberId, wishId)) {
            throw new RequestFailure(NOT_FOUND, "위시 리스트 항목을 찾을 수 없습니다.");
        }
    }

    private RequestFailure addFailure(long productId) {
        if (!products.existsProduct(productId)) {
            return new RequestFailure(NOT_FOUND, "상품을 찾을 수 없습니다.");
        }
        return new RequestFailure(CONFLICT, "이미 위시 리스트에 있는 상품입니다.");
    }
}
