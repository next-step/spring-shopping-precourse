package shopping.wish;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import shopping.product.Product;

class WishTest {

    private final Product product = new Product("사과", 1000, "https://example.com/apple.png");

    @Test
    void 위시를_담은_회원이_주인이다() {
        Wish wish = new Wish(1L, product);

        assertThat(wish.isOwnedBy(1L)).isTrue();
    }

    @Test
    void 다른_회원은_주인이_아니다() {
        Wish wish = new Wish(1L, product);

        assertThat(wish.isOwnedBy(2L)).isFalse();
    }
}
