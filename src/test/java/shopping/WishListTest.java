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

        var wish = wishList.registeredWish(token, product.id());

        Assertions.assertThat(wishList.items(token)).containsExactly(new WishItem(wish.id(), product, 1));
        Assertions.assertThat(wishList.items(tokens.token(2L))).isEmpty();
    }

    @Test
    void 삭제된_상품은_목록에서_숨긴다() {
        String token = tokens.token(1L);
        var product = products.registeredProduct("치이카와", 1000L, "www.example.com");
        wishList.registeredWish(token, product.id());

        products.remove(product.id());

        Assertions.assertThat(wishList.items(token)).isEmpty();
    }

    @Test
    void 미등록_토큰으로는_상품을_담을_수_없다() {
        var product = products.registeredProduct("치이카와", 1000L, "www.example.com");

        Assertions.assertThatThrownBy(() -> wishList.registeredWish("unknown", product.id()))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 수량이_2인_항목도_한번에_삭제한다() {
        String token = tokens.token(1L);
        var product = products.registeredProduct("치이카와", 1000L, "www.example.com");
        wishList.registeredWish(token, product.id());
        var wish = wishList.registeredWish(token, product.id());

        wishList.remove(token, wish.id());

        Assertions.assertThat(wishList.items(token)).isEmpty();
    }

    @Test
    void 다른_회원의_항목은_삭제할_수_없다() {
        String ownerToken = tokens.token(1L);
        String otherToken = tokens.token(2L);
        var product = products.registeredProduct("치이카와", 1000L, "www.example.com");
        var wish = wishList.registeredWish(ownerToken, product.id());

        Assertions.assertThatThrownBy(() -> wishList.remove(otherToken, wish.id()))
                .isInstanceOf(IllegalArgumentException.class);
        Assertions.assertThat(wishList.items(ownerToken)).containsExactly(new WishItem(wish.id(), product, 1));
    }

    // 없는 위시 ID와 미등록 토큰으로 삭제하는 경우도 테스트할 수 있다. 생략.
    // 없는 상품을 담는 경우도 테스트할 수 있다. 생략.
}
