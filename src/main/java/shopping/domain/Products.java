package shopping.domain;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

public class Products {
    // 모든 공개 메서드가 같은 락(this)을 쓴다.
    // 중복 검사와 추가가 한 덩어리로 실행되어야 동시 요청에서도 중복을 막을 수 있다.
    // 상품 ID를 키로 쓰므로 같은 ID의 상품은 하나만 담긴다. 이름 중복은 따로 검사한다.
    private final Map<ProductId, Product> values = new LinkedHashMap<>();

    public synchronized void add(Product product) {
        validateNotNull(product, "상품 정보가 없습니다.");
        ProductId id = idOf(product);
        validateIdNotDuplicated(id);
        validateNameNotDuplicated(product.name(), id);
        values.put(id, product);
    }

    public synchronized List<Product> all() {
        return List.copyOf(values.values());
    }

    public synchronized Optional<Product> findById(ProductId id) {
        validateNotNull(id, "상품 번호가 없습니다.");
        return Optional.ofNullable(values.get(id));
    }

    public synchronized void update(ProductId id, ProductName name, ProductPrice price,
            ProductImage image) {
        Product product = getById(id);
        validateNameNotDuplicated(name, id);
        product.update(name, price, image);
    }

    public synchronized void remove(ProductId id) {
        getById(id);
        values.remove(id);
    }

    private Product getById(ProductId id) {
        return findById(id).orElseThrow(
                () -> new NoSuchElementException("존재하지 않는 상품입니다. (상품 번호: " + id.value() + ")"));
    }

    private ProductId idOf(Product product) {
        return product.id().orElseThrow(
                () -> new IllegalArgumentException("아직 등록되지 않은 상품은 추가할 수 없습니다."));
    }

    private void validateIdNotDuplicated(ProductId id) {
        if (values.containsKey(id)) {
            throw new IllegalArgumentException("이미 등록된 상품 번호입니다: " + id.value());
        }
    }

    private void validateNameNotDuplicated(ProductName name, ProductId ownId) {
        boolean taken = values.values().stream()
                .anyMatch(product -> product.hasName(name) && !product.hasId(ownId));
        if (taken) {
            throw new IllegalArgumentException("이미 같은 이름의 상품이 있습니다: " + name.value());
        }
    }

    private static void validateNotNull(Object value, String message) {
        if (value == null) {
            throw new NullPointerException(message);
        }
    }
}
