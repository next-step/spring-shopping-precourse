package shopping.product;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.util.regex.Pattern;

@Entity
public class Product {
    private static final int MAX_NAME_LENGTH = 15;
    private static final Pattern NAME_PATTERN = Pattern.compile("^[\\p{L}\\p{N} ()\\[\\]+\\-&/_]+$");
    private static final Pattern URL_PATTERN = Pattern.compile("^https?://\\S+$");

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private int price;

    private String imageUrl;

    protected Product() {
    }

    public Product(String name, Integer price, String imageUrl) {
        update(name, price, imageUrl);
    }

    public void update(String name, Integer price, String imageUrl) {
        validateName(name);
        validatePrice(price);
        validateImageUrl(imageUrl);
        this.name = name;
        this.price = price;
        this.imageUrl = imageUrl;
    }

    private void validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("상품 이름은 비어 있을 수 없습니다.");
        }
        if (name.codePointCount(0, name.length()) > MAX_NAME_LENGTH) {
            throw new IllegalArgumentException("상품 이름은 공백을 포함하여 최대 15자까지 입력할 수 있습니다.");
        }
        if (!NAME_PATTERN.matcher(name).matches()) {
            throw new IllegalArgumentException("상품 이름에는 ( ), [ ], +, -, &, /, _ 외의 특수 문자를 사용할 수 없습니다.");
        }
    }

    private void validatePrice(Integer price) {
        if (price == null || price < 1) {
            throw new IllegalArgumentException("상품 가격은 1원 이상이어야 합니다.");
        }
    }

    private void validateImageUrl(String imageUrl) {
        if (imageUrl == null || !URL_PATTERN.matcher(imageUrl).matches()) {
            throw new IllegalArgumentException("이미지 URL은 http:// 또는 https://로 시작해야 합니다.");
        }
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
