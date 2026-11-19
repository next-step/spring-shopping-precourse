package shopping.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;

public class Products {
    private final List<Product> values = new ArrayList<>();

    public void add(Product product) {
        Objects.requireNonNull(product, "상품은 null일 수 없습니다.");
        ProductId id = idOf(product);
        validateIdNotDuplicated(id);
        validateNameNotDuplicated(product.name(), id);
        values.add(product);
    }

    public List<Product> all() {
        return List.copyOf(values);
    }

    public Optional<Product> findById(ProductId id) {
        return values.stream()
                .filter(product -> product.id().equals(Optional.of(id)))
                .findFirst();
    }

    public void update(ProductId id, ProductName name, ProductPrice price, ProductImage image) {
        Product product = getById(id);
        validateNameNotDuplicated(name, id);
        product.update(name, price, image);
    }

    public void remove(ProductId id) {
        values.remove(getById(id));
    }

    private Product getById(ProductId id) {
        return findById(id).orElseThrow(
                () -> new NoSuchElementException("존재하지 않는 상품입니다. id=" + id.value()));
    }

    private ProductId idOf(Product product) {
        return product.id().orElseThrow(
                () -> new IllegalArgumentException("ID가 없는 상품은 추가할 수 없습니다."));
    }

    private void validateIdNotDuplicated(ProductId id) {
        if (findById(id).isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 상품 ID입니다. id=" + id.value());
        }
    }

    private void validateNameNotDuplicated(ProductName name, ProductId ownId) {
        boolean taken = values.stream()
                .anyMatch(product -> product.name().equals(name)
                        && !product.id().equals(Optional.of(ownId)));
        if (taken) {
            throw new IllegalArgumentException("이미 존재하는 상품 이름입니다. name=" + name.value());
        }
    }
}
