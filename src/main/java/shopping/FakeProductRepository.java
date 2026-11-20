package shopping;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.jspecify.annotations.Nullable;

public final class FakeProductRepository implements ProductRepository {
    private final Map<Long, Product> products = new LinkedHashMap<>();
    private long nextId = 1L;

    @Override
    public synchronized Product registeredProduct(String name, long price, String url) {
        var product = new Product(nextId, name, price, url);
        products.put(product.id(), product);
        nextId++;
        return product;
    }

    @Override
    public synchronized @Nullable Product product(Long id) {
        return products.get(id);
    }

    @Override
    public synchronized List<Product> products() {
        return List.copyOf(products.values());
    }
}
