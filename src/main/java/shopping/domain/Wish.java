package shopping.domain;

public record Wish(Long id, Long memberId, Long productId) {

    public boolean isOwnedBy(Long memberId) {
        return this.memberId.equals(memberId);
    }
}
