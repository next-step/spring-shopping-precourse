package shopping.repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Repository;
import shopping.model.Wish;

@Repository
public class WishRepository {
    private final AtomicLong sequence = new AtomicLong();
    private final Map<Long, Wish> wishes = new ConcurrentHashMap<>();

    public synchronized Wish save(Wish wish) {
        wish.assignId(sequence.incrementAndGet());
        wishes.put(wish.getId(), wish);
        return wish;
    }

    public Optional<Wish> findByIdAndMemberId(Long wishId, Long memberId) {
        return Optional.ofNullable(wishes.get(wishId))
                .filter(wish -> wish.belongsTo(memberId));
    }

    public List<Wish> findAllByMemberId(Long memberId) {
        return wishes.values().stream()
                .filter(wish -> wish.belongsTo(memberId))
                .toList();
    }

    public void delete(Wish wish) {
        wishes.remove(wish.getId());
    }
}
