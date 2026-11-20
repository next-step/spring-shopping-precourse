package shopping.domain;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

public class WishList {
    // 같은 회원이 동시에 요청해도 중복 검사와 추가가 한 덩어리로 실행되도록 같은 락을 쓴다.
    // 상품 ID를 키로 쓰므로 같은 ID의 상품은 하나만 담긴다.
    private final Map<ProductId, Product> values = new LinkedHashMap<>();

    public synchronized void add(Product product) {
        validateNotNull(product, "상품 정보가 없습니다.");
        ProductId id = idOf(product);
        validateNotDuplicated(id);
        values.put(id, product);
    }

    public synchronized void remove(ProductId id) {
        if (!values.containsKey(id)) {
            throw new NoSuchElementException("위시 리스트에 없는 상품입니다. (상품 번호: " + id.value() + ")");
        }
        values.remove(id);
    }

    public synchronized List<Product> all() {
        return List.copyOf(values.values());
    }

    private ProductId idOf(Product product) {
        return product.id().orElseThrow(
                () -> new IllegalArgumentException("아직 등록되지 않은 상품은 추가할 수 없습니다."));
    }

    private void validateNotDuplicated(ProductId id) {
        if (values.containsKey(id)) {
            throw new IllegalArgumentException("이미 위시 리스트에 담은 상품입니다.");
        }
    }

    private static void validateNotNull(Object value, String message) {
        if (value == null) {
            throw new NullPointerException(message);
        }
    }
}
