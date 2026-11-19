package shopping.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.NoSuchElementException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ProductsTest {
    private static final ProfanityChecker NO_PROFANITY = text -> false;
    private static final String URL = "https://example.com/a.png";

    private Products products;

    private static Product product(long id, String name) {
        return new Product(new ProductId(id), new ProductName(name, NO_PROFANITY),
                new ProductPrice(1000), new ProductImage(URL));
    }

    private static ProductName name(String value) {
        return new ProductName(value, NO_PROFANITY);
    }

    @BeforeEach
    void setUp() {
        products = new Products();
    }

    @Test
    @DisplayName("처음에는 비어 있다")
    void empty() {
        assertThat(products.all()).isEmpty();
    }

    @Test
    @DisplayName("상품을 추가하면 추가한 순서대로 조회된다")
    void add() {
        Product first = product(1, "First");
        Product second = product(2, "Second");

        products.add(first);
        products.add(second);

        assertThat(products.all()).containsExactly(first, second);
    }

    @Test
    @DisplayName("조회한 목록을 직접 수정할 수 없다")
    void allIsUnmodifiable() {
        products.add(product(1, "First"));

        assertThatThrownBy(() -> products.all().clear())
                .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    @DisplayName("ID가 없는 상품은 추가할 수 없다")
    void addWithoutId() {
        Product unsaved = new Product(name("Item"), new ProductPrice(1), new ProductImage(URL));

        assertThatThrownBy(() -> products.add(unsaved))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("null은 추가할 수 없다")
    void addNull() {
        assertThatThrownBy(() -> products.add(null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("같은 ID의 상품은 추가할 수 없다")
    void addDuplicateId() {
        products.add(product(1, "First"));

        assertThatThrownBy(() -> products.add(product(1, "Other")))
                .isInstanceOf(IllegalArgumentException.class);
        assertThat(products.all()).hasSize(1);
    }

    @Test
    @DisplayName("같은 이름의 상품은 추가할 수 없다")
    void addDuplicateName() {
        products.add(product(1, "First"));

        assertThatThrownBy(() -> products.add(product(2, "First")))
                .isInstanceOf(IllegalArgumentException.class);
        assertThat(products.all()).hasSize(1);
    }

    @Test
    @DisplayName("ID로 상품을 찾을 수 있고 없으면 비어 있다")
    void findById() {
        Product first = product(1, "First");
        products.add(first);

        assertThat(products.findById(new ProductId(1))).contains(first);
        assertThat(products.findById(new ProductId(2))).isEmpty();
    }

    @Test
    @DisplayName("상품을 수정할 수 있다")
    void update() {
        products.add(product(1, "First"));

        products.update(new ProductId(1), name("Renamed"), new ProductPrice(5),
                new ProductImage("https://b.com"));

        Product updated = products.findById(new ProductId(1)).orElseThrow();
        assertThat(updated.name()).isEqualTo(name("Renamed"));
        assertThat(updated.price().value()).isEqualTo(5);
    }

    @Test
    @DisplayName("자기 자신의 이름으로 수정하는 것은 허용한다")
    void updateWithOwnName() {
        products.add(product(1, "First"));

        products.update(new ProductId(1), name("First"), new ProductPrice(5),
                new ProductImage(URL));

        assertThat(products.findById(new ProductId(1)).orElseThrow().price().value())
                .isEqualTo(5);
    }

    @Test
    @DisplayName("다른 상품과 같은 이름으로 수정하면 예외가 발생하고 기존 값은 유지된다")
    void updateDuplicateName() {
        products.add(product(1, "First"));
        products.add(product(2, "Second"));

        assertThatThrownBy(() -> products.update(new ProductId(2), name("First"),
                new ProductPrice(5), new ProductImage(URL)))
                .isInstanceOf(IllegalArgumentException.class);

        Product second = products.findById(new ProductId(2)).orElseThrow();
        assertThat(second.name()).isEqualTo(name("Second"));
        assertThat(second.price().value()).isEqualTo(1000);
    }

    @Test
    @DisplayName("없는 상품을 수정하면 예외가 발생한다")
    void updateNotFound() {
        assertThatThrownBy(() -> products.update(new ProductId(9), name("Item"),
                new ProductPrice(5), new ProductImage(URL)))
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    @DisplayName("상품을 삭제할 수 있고, 삭제한 상품의 이름과 ID는 다시 쓸 수 있다")
    void remove() {
        products.add(product(1, "First"));

        products.remove(new ProductId(1));

        assertThat(products.all()).isEmpty();
        products.add(product(1, "First"));
        assertThat(products.all()).hasSize(1);
    }

    @Test
    @DisplayName("없는 상품을 삭제하면 예외가 발생한다")
    void removeNotFound() {
        assertThatThrownBy(() -> products.remove(new ProductId(9)))
                .isInstanceOf(NoSuchElementException.class);
    }
}
