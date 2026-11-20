package shopping.model;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class WishTest {
    private final Member member = new Member(
            new Email("user@example.com"), new PasswordHash("encoded-password"));
    private final Product product = new Product(
            new ProductName("상품"), 1_000L, "image-url");

    @Test
    @DisplayName("위시리스트 회원은 null일 수 없다")
    void rejectNullMember() {
        assertThatThrownBy(() -> new Wish(null, product))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("위시리스트 상품은 null일 수 없다")
    void rejectNullProduct() {
        assertThatThrownBy(() -> new Wish(member, null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("위시리스트 ID는 null일 수 없다")
    void rejectNullId() {
        Wish wish = new Wish(member, product);

        assertThatThrownBy(() -> wish.assignId(null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("회원 확인에 사용하는 ID는 null일 수 없다")
    void rejectNullMemberId() {
        Wish wish = new Wish(member, product);

        assertThatThrownBy(() -> wish.belongsTo(null))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
