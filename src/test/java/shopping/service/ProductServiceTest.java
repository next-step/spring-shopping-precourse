package shopping.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.NoSuchElementException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import shopping.domain.Product;
import shopping.domain.ProductId;
import shopping.domain.Products;
import shopping.domain.ProfanityChecker;

class ProductServiceTest {
    private static final ProfanityChecker CHECKER = text -> text.contains("shit");
    private static final String URL = "https://example.com/a.png";

    private ProductService service;

    @BeforeEach
    void setUp() {
        service = new ProductService(new Products(), CHECKER);
    }

    @Test
    @DisplayName("상품을 생성하면 1부터 순서대로 ID가 부여된다")
    void createAssignsIds() {
        Product first = service.create("First", 1000, URL);
        Product second = service.create("Second", 2000, URL);

        assertThat(first.id()).contains(new ProductId(1));
        assertThat(second.id()).contains(new ProductId(2));
        assertThat(first.name().value()).isEqualTo("First");
        assertThat(first.price().value()).isEqualTo(1000);
        assertThat(first.image().url()).isEqualTo(URL);
    }

    @Test
    @DisplayName("규칙을 어긴 값으로는 상품을 만들 수 없고 ID도 소비하지 않는다")
    void createInvalid() {
        assertThatThrownBy(() -> service.create("bad!", 1000, URL))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> service.create("Item", -1, URL))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> service.create("Item", 1, "http://a.com"))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> service.create(null, 1, URL))
                .isInstanceOf(IllegalArgumentException.class);

        assertThat(service.list()).isEmpty();
        assertThat(service.create("Item", 1, URL).id()).contains(new ProductId(1));
    }

    @Test
    @DisplayName("비속어가 포함된 이름으로는 상품을 만들 수 없다")
    void createWithProfanity() {
        assertThatThrownBy(() -> service.create("shit", 1000, URL))
                .isInstanceOf(IllegalArgumentException.class);

        assertThat(service.list()).isEmpty();
    }

    @Test
    @DisplayName("같은 이름의 상품은 만들 수 없고 ID도 소비하지 않는다")
    void createDuplicateName() {
        service.create("Same", 1000, URL);

        assertThatThrownBy(() -> service.create("Same", 2000, URL))
                .isInstanceOf(IllegalArgumentException.class);

        assertThat(service.list()).hasSize(1);
        assertThat(service.create("Other", 1, URL).id()).contains(new ProductId(2));
    }

    @Test
    @DisplayName("ID로 상품을 조회하고 없으면 예외가 발생한다")
    void get() {
        Product created = service.create("First", 1000, URL);

        assertThat(service.get(new ProductId(1))).isSameAs(created);
        assertThatThrownBy(() -> service.get(new ProductId(9)))
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    @DisplayName("상품 목록은 생성한 순서대로 조회된다")
    void list() {
        Product first = service.create("First", 1, URL);
        Product second = service.create("Second", 2, URL);

        assertThat(service.list()).containsExactly(first, second);
    }

    @Test
    @DisplayName("상품을 수정하면 이름, 가격, 이미지가 바뀌고 ID는 유지된다")
    void update() {
        service.create("First", 1000, URL);

        Product updated = service.update(new ProductId(1), "Renamed", 5, "https://b.com/b.png");

        assertThat(updated.id()).contains(new ProductId(1));
        assertThat(updated.name().value()).isEqualTo("Renamed");
        assertThat(updated.price().value()).isEqualTo(5);
        assertThat(updated.image().url()).isEqualTo("https://b.com/b.png");
    }

    @Test
    @DisplayName("자기 자신의 이름으로 수정하는 것은 허용한다")
    void updateWithOwnName() {
        service.create("First", 1000, URL);

        Product updated = service.update(new ProductId(1), "First", 5, URL);

        assertThat(updated.price().value()).isEqualTo(5);
    }

    @Test
    @DisplayName("수정할 값이 규칙을 어기면 예외가 발생하고 기존 값은 유지된다")
    void updateInvalid() {
        service.create("First", 1000, URL);

        assertThatThrownBy(() -> service.update(new ProductId(1), "bad!", 5, URL))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> service.update(new ProductId(1), "shit", 5, URL))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> service.update(new ProductId(1), "Item", -1, URL))
                .isInstanceOf(IllegalArgumentException.class);

        Product product = service.get(new ProductId(1));
        assertThat(product.name().value()).isEqualTo("First");
        assertThat(product.price().value()).isEqualTo(1000);
    }

    @Test
    @DisplayName("다른 상품과 같은 이름으로 수정하면 예외가 발생하고 기존 값은 유지된다")
    void updateDuplicateName() {
        service.create("First", 1000, URL);
        service.create("Second", 2000, URL);

        assertThatThrownBy(() -> service.update(new ProductId(2), "First", 5, URL))
                .isInstanceOf(IllegalArgumentException.class);

        assertThat(service.get(new ProductId(2)).name().value()).isEqualTo("Second");
    }

    @Test
    @DisplayName("없는 상품을 수정하면 예외가 발생한다")
    void updateNotFound() {
        assertThatThrownBy(() -> service.update(new ProductId(9), "Item", 5, URL))
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    @DisplayName("상품을 삭제하면 조회되지 않고 같은 이름을 다시 쓸 수 있다")
    void delete() {
        service.create("First", 1000, URL);

        service.delete(new ProductId(1));

        assertThat(service.list()).isEmpty();
        assertThat(service.create("First", 1, URL).id()).contains(new ProductId(2));
    }

    @Test
    @DisplayName("없는 상품을 삭제하면 예외가 발생한다")
    void deleteNotFound() {
        assertThatThrownBy(() -> service.delete(new ProductId(9)))
                .isInstanceOf(NoSuchElementException.class);
    }
}
