package shopping;

public record WishResponse(Long id, ProductResponse product) {

    public static WishResponse from(Wish wish) {
        return new WishResponse(wish.getId(), ProductResponse.from(wish.getProduct()));
    }
}
