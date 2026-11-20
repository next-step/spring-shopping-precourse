package shopping;

import java.util.ArrayList;
import java.util.List;

public final class WishList {
    private final TokenRepository tokens;
    private final ProductRepository products;
    private final WishRepository wishes;

    public WishList(TokenRepository tokens, ProductRepository products, WishRepository wishes) {
        this.tokens = tokens;
        this.products = products;
        this.wishes = wishes;
    }

    public Wish registeredWish(String token, Long productId) {
        Long memberId = memberId(token);
        if (products.product(productId) == null) {
            throw new IllegalArgumentException("존재하지 않는 상품입니다");
        }
        return wishes.registeredWish(memberId, productId);
    }

    public List<WishItem> items(String token) {
        var items = new ArrayList<WishItem>();
        for (Wish wish : wishes.wishes(memberId(token))) {
            Product product = products.product(wish.productId());
            if (product != null) {
                items.add(new WishItem(wish.id(), product, wish.quantity()));
            }
        }
        return List.copyOf(items);
    }

    public void remove(String token, Long wishId) {
        wishes.remove(memberId(token), wishId);
    }

    private Long memberId(String token) {
        if (token == null || token.isBlank()) {
            throw new IllegalArgumentException("유효하지 않은 인증 토큰입니다");
        }
        Long memberId = tokens.memberId(token);
        if (memberId == null) {
            throw new IllegalArgumentException("유효하지 않은 인증 토큰입니다");
        }
        return memberId;
    }
}
