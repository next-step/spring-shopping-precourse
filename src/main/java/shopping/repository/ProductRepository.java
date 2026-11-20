package shopping.repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Repository;
import shopping.model.Product;

@Repository
public class ProductRepository {
    private final AtomicLong sequence = new AtomicLong();
    private final Map<Long, Product> products = new ConcurrentHashMap<>();

    public synchronized Product save(Product product) {
        product.assignId(sequence.incrementAndGet());
        products.put(product.getId(), product);
        return product;
    }

    public Optional<Product> findById(Long productId) {
        return Optional.ofNullable(products.get(productId));
    }

    public List<Product> findAll() {
        return List.copyOf(products.values());
    }

    public void delete(Product product) {
        products.remove(product.getId());
    }
}
