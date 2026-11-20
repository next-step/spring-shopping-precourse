package shopping.repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import shopping.domain.Wish;

@Repository
public class WishRepository {

    private final Map<Long, Wish> wishes = new HashMap<>();
    private long sequence = 1L;

    public Wish save(Wish wish) {
        Wish savedWish = new Wish(sequence, wish.memberId(), wish.productId());
        wishes.put(sequence, savedWish);
        sequence++;

        return savedWish;
    }

    public Wish findById(Long id) {
        return wishes.get(id);
    }

    public void deleteById(Long id) {
        wishes.remove(id);
    }

    public List<Wish> findAllByMemberId(Long memberId) {
        return wishes.values().stream()
                .filter(wish -> wish.isOwnedBy(memberId))
                .toList();
    }
}
