package shopping;

public record ProductResponse(Long id, String name, long price, String url) {
    static ProductResponse response(Product product) {
        return new ProductResponse(product.id(), product.name(), product.price(), product.url());
    }
}
