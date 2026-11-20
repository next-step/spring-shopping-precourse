package shopping.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.NoSuchElementException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class WishListTest {
    private static final ProfanityChecker NO_PROFANITY = text -> false;

    private WishList wishList;

    private static Product product(long id, String name) {
        return new Product(new ProductId(id), new ProductName(name, NO_PROFANITY),
                new ProductPrice(1000), new ProductImage("https://example.com/a.png"));
    }

    @BeforeEach
    void setUp() {
        wishList = new WishList();
    }

    @Test
    @DisplayName("처음에는 비어 있다")
    void empty() {
        assertThat(wishList.all()).isEmpty();
    }

    @Test
    @DisplayName("상품을 추가하면 담은 순서대로 조회된다")
    void add() {
        Product first = product(1, "First");
        Product second = product(2, "Second");

        wishList.add(first);
        wishList.add(second);

        assertThat(wishList.all()).containsExactly(first, second);
    }

    @Test
    @DisplayName("같은 ID의 상품은 추가할 수 없고 기존 목록은 유지된다")
    void addDuplicateId() {
        Product first = product(1, "First");
        wishList.add(first);

        assertThatThrownBy(() -> wishList.add(product(1, "Other")))
                .isInstanceOf(IllegalArgumentException.class);
        assertThat(wishList.all()).containsExactly(first);
    }

    @Test
    @DisplayName("같은 상품 객체를 두 번 추가할 수 없다")
    void addSameProductTwice() {
        Product first = product(1, "First");
        wishList.add(first);

        assertThatThrownBy(() -> wishList.add(first))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("ID가 다르면 이름이 같아도 추가할 수 있다")
    void addSameNameDifferentId() {
        wishList.add(product(1, "Same"));

        wishList.add(product(2, "Same"));

        assertThat(wishList.all()).hasSize(2);
    }

    @Test
    @DisplayName("ID가 없는 상품은 추가할 수 없다")
    void addWithoutId() {
        Product unsaved = new Product(new ProductName("Item", NO_PROFANITY),
                new ProductPrice(1), new ProductImage("https://a.com"));

        assertThatThrownBy(() -> wishList.add(unsaved))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("null은 추가할 수 없다")
    void addNull() {
        assertThatThrownBy(() -> wishList.add(null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("상품 ID로 삭제할 수 있다")
    void remove() {
        Product first = product(1, "First");
        Product second = product(2, "Second");
        wishList.add(first);
        wishList.add(second);

        wishList.remove(new ProductId(1));

        assertThat(wishList.all()).containsExactly(second);
    }

    @Test
    @DisplayName("담겨 있지 않은 상품을 삭제하면 예외가 발생한다")
    void removeNotFound() {
        wishList.add(product(1, "First"));

        assertThatThrownBy(() -> wishList.remove(new ProductId(9)))
                .isInstanceOf(NoSuchElementException.class);
        assertThat(wishList.all()).hasSize(1);
    }

    @Test
    @DisplayName("삭제한 상품은 다시 추가할 수 있다")
    void addAfterRemove() {
        wishList.add(product(1, "First"));
        wishList.remove(new ProductId(1));

        wishList.add(product(1, "First"));

        assertThat(wishList.all()).hasSize(1);
    }

    @Test
    @DisplayName("조회한 목록을 직접 수정할 수 없다")
    void allIsUnmodifiable() {
        wishList.add(product(1, "First"));

        assertThatThrownBy(() -> wishList.all().clear())
                .isInstanceOf(UnsupportedOperationException.class);
    }
}
