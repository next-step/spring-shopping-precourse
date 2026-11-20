package shopping.wish;

public class Wish {
    private final Long id;
    private final Long memberId;
    private final Long productId;

    public Wish(Long id, Long memberId, Long productId) {
        this.id = id;
        this.memberId = memberId;
        this.productId = productId;
    }

    public Wish withId(Long newId) {
        return new Wish(newId, memberId, productId);
    }

    public boolean isOwnedBy(Long targetMemberId) {
        return memberId.equals(targetMemberId);
    }

    public boolean hasProduct(Long targetProductId) {
        return productId.equals(targetProductId);
    }

    public Long getId() {
        return id;
    }

    public Long getProductId() {
        return productId;
    }
}