package shopping;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class FakeWishRepository implements WishRepository {
    private final Map<Long, Map<Long, Wish>> wishesByMember = new LinkedHashMap<>();
    private long nextId = 1L;

    @Override
    public synchronized Wish registered(Long memberId, Long productId) {
        var wishes = wishesByMember.computeIfAbsent(memberId, id -> new LinkedHashMap<>());
        var existing = wishes.get(productId);
        if (existing != null) {
            var increased = existing.increased();
            wishes.put(productId, increased);
            return increased;
        }
        var wish = new Wish(nextId++, memberId, productId, 1);
        wishes.put(productId, wish);
        return wish;
    }

    @Override
    public synchronized List<Wish> wishes(Long memberId) {
        return List.copyOf(wishesByMember.getOrDefault(memberId, Map.of()).values());
    }
}
