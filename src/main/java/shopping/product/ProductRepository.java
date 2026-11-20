package shopping.product;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Repository;

@Repository
public class ProductRepository {
    private final Map<Long, Product> products = new ConcurrentHashMap<>();
    private final AtomicLong sequence = new AtomicLong();

    public Product save(Product product) {
        Product saved = product.withId(resolveId(product));
        products.put(saved.getId(), saved);
        return saved;
    }

    public Optional<Product> findById(Long id) {
        return Optional.ofNullable(products.get(id));
    }

    public List<Product> findAll() {
        return products.values().stream()
                .sorted(Comparator.comparing(Product::getId))
                .toList();
    }

    public void deleteById(Long id) {
        products.remove(id);
    }

    private Long resolveId(Product product) {
        if (product.getId() == null) {
            return sequence.incrementAndGet();
        }
        return product.getId();
    }
}