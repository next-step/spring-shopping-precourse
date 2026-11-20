package shopping;

public record ProductRequest(String name, long price, String imageUrl) {

    public Product toEntity() {
        return new Product(name, price, imageUrl);
    }
}
