package shopping.wish;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Repository;

@Repository
public class WishRepository {
    private final Map<Long, Wish> wishes = new ConcurrentHashMap<>();
    private final AtomicLong sequence = new AtomicLong();

    public Wish save(Wish wish) {
        Wish saved = wish.withId(sequence.incrementAndGet());
        wishes.put(saved.getId(), saved);
        return saved;
    }

    public Optional<Wish> findById(Long id) {
        return Optional.ofNullable(wishes.get(id));
    }

    public List<Wish> findByMemberId(Long memberId) {
        return wishes.values().stream()
                .filter(wish -> wish.isOwnedBy(memberId))
                .sorted(Comparator.comparing(Wish::getId))
                .toList();
    }

    public boolean existsByMemberIdAndProductId(Long memberId, Long productId) {
        return wishes.values().stream()
                .anyMatch(wish -> wish.isOwnedBy(memberId) && wish.hasProduct(productId));
    }

    public void deleteById(Long id) {
        wishes.remove(id);
    }
}