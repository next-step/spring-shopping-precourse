package shopping;

import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class WishListTest {
    private final TokenRepository tokens = new InMemoryTokenRepository();
    private final ProductRepository products =
            new FakeProductRepository(new DefaultProductNameFilter(List.of()));
    private final WishList wishList = new WishList(tokens, products, new FakeWishRepository());

    @Test
    void 담은_상품은_내_목록에만_보인다() {
        String token = tokens.token(1L);
        var product = products.registeredProduct("치이카와", 1000L, "www.example.com");

        var wish = wishList.registered(token, product.id());

        Assertions.assertThat(wishList.items(token)).containsExactly(new WishItem(wish.id(), product, 1));
        Assertions.assertThat(wishList.items(tokens.token(2L))).isEmpty();
    }

    @Test
    void 삭제된_상품은_목록에서_숨긴다() {
        String token = tokens.token(1L);
        var product = products.registeredProduct("치이카와", 1000L, "www.example.com");
        wishList.registered(token, product.id());

        products.remove(product.id());

        Assertions.assertThat(wishList.items(token)).isEmpty();
    }

    @Test
    void 미등록_토큰으로는_상품을_담을_수_없다() {
        var product = products.registeredProduct("치이카와", 1000L, "www.example.com");

        Assertions.assertThatThrownBy(() -> wishList.registered("unknown", product.id()))
                .isInstanceOf(IllegalArgumentException.class);
    }

    // 없는 상품을 담는 경우도 테스트할 수 있다. 생략.
}
