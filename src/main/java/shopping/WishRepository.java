package shopping;

import java.util.List;

public interface WishRepository {
    Wish registeredWish(Long memberId, Long productId);

    List<Wish> wishes(Long memberId);

    void remove(Long memberId, Long wishId);
}
