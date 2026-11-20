package shopping.domain;

public class Product{

    private final String productName;
    private final Long productId;
    private final int price;
    private final String imageUrl;

    public Product (String productName, Long productId, int price, String imageUrl) {
        this.productName = productName;
        this.productId = productId;
        this.price = price;
        this.imageUrl = imageUrl;
    }

    public String getProductName() {
        return this.productName;
    }
    public Long getProductId() {
        return this.productId;
    }
    public int getPrice() {
        return this.price;
    }
    public String getImageUrl() {
        return this.imageUrl;
    }
}
