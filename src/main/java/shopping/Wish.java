package shopping;

public record Wish(Long id, Long memberId, Long productId, int quantity) {
    public Wish increased() {
        return new Wish(id, memberId, productId, Math.incrementExact(quantity));
    }
}
