package shopping.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;

public class WishList {
    private final List<Product> values = new ArrayList<>();

    public void add(Product product) {
        Objects.requireNonNull(product, "상품은 null일 수 없습니다.");
        ProductId id = idOf(product);
        validateNotDuplicated(id);
        values.add(product);
    }

    public void remove(ProductId id) {
        Product product = find(id).orElseThrow(
                () -> new NoSuchElementException("위시 리스트에 없는 상품입니다. id=" + id.value()));
        values.remove(product);
    }

    public List<Product> all() {
        return List.copyOf(values);
    }

    private Optional<Product> find(ProductId id) {
        return values.stream()
                .filter(product -> product.id().equals(Optional.of(id)))
                .findFirst();
    }

    private ProductId idOf(Product product) {
        return product.id().orElseThrow(
                () -> new IllegalArgumentException("ID가 없는 상품은 추가할 수 없습니다."));
    }

    private void validateNotDuplicated(ProductId id) {
        if (find(id).isPresent()) {
            throw new IllegalArgumentException("이미 위시 리스트에 있는 상품입니다. id=" + id.value());
        }
    }
}
