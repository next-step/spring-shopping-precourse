package shopping;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.jspecify.annotations.Nullable;

public final class FakeProductRepository implements ProductRepository {
    private final Map<Long, Product> products = new LinkedHashMap<>();
    private final ProductNameFilter productNameFilter;
    private long nextId = 1L;

    public FakeProductRepository(ProductNameFilter productNameFilter) {
        this.productNameFilter = productNameFilter;
    }

    @Override
    public synchronized Product registeredProduct(String name, long price, String url) {
        var product = new Product(nextId, name, price, url, productNameFilter);
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

    @Override
    public synchronized void update(Long id, String name, long price, String url) {
        if (!products.containsKey(id)) {
            throw new IllegalArgumentException("존재하지 않는 상품입니다");
        }
        products.put(id, new Product(id, name, price, url, productNameFilter));
    }

    @Override
    public synchronized void remove(Long id) {
        if (products.remove(id) == null) {
            throw new IllegalArgumentException("존재하지 않는 상품입니다");
        }
    }
}
