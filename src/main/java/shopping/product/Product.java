package shopping.product;

public class Product {
    private final Long id;
    private final ProductName name;
    private final long price;
    private final String imageUrl;

    public Product(Long id, ProductName name, long price, String imageUrl) {
        validatePrice(price);
        validateImageUrl(imageUrl);
        this.id = id;
        this.name = name;
        this.price = price;
        this.imageUrl = imageUrl;
    }

    public Product withId(Long newId) {
        return new Product(newId, name, price, imageUrl);
    }

    public Product update(ProductName newName, long newPrice, String newImageUrl) {
        return new Product(id, newName, newPrice, newImageUrl);
    }

    public Long getId() {
        return id;
    }

    public ProductName getName() {
        return name;
    }

    public long getPrice() {
        return price;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    private static void validatePrice(long price) {
        if (price < 0) {
            throw new IllegalArgumentException("상품 가격은 0원 이상이어야 합니다.");
        }
    }

    private static void validateImageUrl(String imageUrl) {
        if (imageUrl == null || !imageUrl.matches("^https?://.+")) {
            throw new IllegalArgumentException("이미지 URL은 http 또는 https로 시작해야 합니다.");
        }
    }
}