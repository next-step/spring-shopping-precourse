package shopping;

import java.util.List;

public interface WishRepository {
    Wish registered(Long memberId, Long productId);

    List<Wish> wishes(Long memberId);
}
