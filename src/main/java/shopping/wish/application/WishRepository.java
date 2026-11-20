package shopping.wish.application;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import shopping.wish.domain.Wish;

public interface WishRepository {
    List<ListedWish> findByMemberId(long memberId);

    Optional<Long> add(Wish wish);

    boolean delete(long memberId, long wishId);

    record ListedWish(long wishId, long productId, String name, BigDecimal price, String imageUrl) {}
}
