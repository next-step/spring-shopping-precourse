package shopping.product;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "products")
public class Product {

    private static final int MIN_PRICE = 0;
    private static final String HTTP_PREFIX = "http://";
    private static final String HTTPS_PREFIX = "https://";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private int price;

    @Column(name = "image_url")
    private String imageUrl;

    protected Product() {
        // JPA가 DB에서 꺼낸 데이터로 객체를 만들 때 쓰는 생성자 (비워 둠)
    }

    public Product(String name, Integer price, String imageUrl) {
        change(name, price, imageUrl);
    }

    public void update(String name, Integer price, String imageUrl) {
        change(name, price, imageUrl);
    }

    private void change(String name, Integer price, String imageUrl) {
        ProductName productName = new ProductName(name);
        validatePrice(price);
        validateImageUrl(imageUrl);
        this.name = productName.getValue();
        this.price = price;
        this.imageUrl = imageUrl;
    }

    private void validatePrice(Integer price) {
        if (price == null || price < MIN_PRICE) {
            throw new IllegalArgumentException("상품 가격은 0원 이상이어야 합니다.");
        }
    }

    private void validateImageUrl(String imageUrl) {
        if (imageUrl == null || imageUrl.isBlank() || !isHttpUrl(imageUrl)) {
            throw new IllegalArgumentException("이미지 URL은 http:// 또는 https://로 시작해야 합니다.");
        }
    }

    private boolean isHttpUrl(String url) {
        return url.startsWith(HTTP_PREFIX) || url.startsWith(HTTPS_PREFIX);
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getPrice() {
        return price;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}