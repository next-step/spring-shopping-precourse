package shopping.product.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import java.net.URI;
import java.util.regex.Pattern;
import shopping.common.error.ErrorCode;
import shopping.common.error.ShoppingException;

@Entity
@Table(name = "products")
public class Product {

    private static final Pattern NAME = Pattern.compile("[\\p{L}\\p{N} ()\\[\\]+&/_-]+");
    private static final int MAX_NAME_LENGTH = 15;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 30)
    private String name;
    @Column(nullable = false)
    private Integer price;
    @Lob
    @Column(nullable = false)
    private String imageUrl;

    protected Product() {
    }

    public Product(String name, Integer price, String imageUrl) {
        validate(name, price, imageUrl);
        apply(name, price, imageUrl);
    }

    public void changeTo(String name, Integer price, String imageUrl, ProfanityChecker checker) {
        validate(name, price, imageUrl);
        checkProfanity(name, checker);

        apply(name, price, imageUrl);
    }

    public void checkProfanity(ProfanityChecker checker) {
        checkProfanity(name, checker);
    }

    private void apply(String name, Integer price, String imageUrl) {
        this.name = name;
        this.price = price;
        this.imageUrl = imageUrl;
    }

    private static void validate(String name, Integer price, String imageUrl) {
        validateName(name);
        validatePrice(price);
        validateImageUrl(imageUrl);
    }

    private static void validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("상품 이름은 필수입니다.");
        }
        if (name.codePointCount(0, name.length()) > MAX_NAME_LENGTH
                || !NAME.matcher(name).matches()) {
            throw new IllegalArgumentException("상품 이름의 길이 또는 문자가 올바르지 않습니다.");
        }
    }

    private static void validatePrice(Integer price) {
        if (price == null || price < 1) {
            throw new IllegalArgumentException("가격은 1원 이상의 정수여야 합니다.");
        }
    }

    private static void validateImageUrl(String url) {
        if (url == null || url.isBlank()) {
            throw new IllegalArgumentException("이미지 URL은 필수입니다.");
        }

        URI uri = URI.create(url);
        boolean http = "http".equalsIgnoreCase(uri.getScheme())
                || "https".equalsIgnoreCase(uri.getScheme());
        if (!http || uri.getHost() == null) {
            throw new IllegalArgumentException("호스트가 있는 HTTP URL이어야 합니다.");
        }
    }

    private static void checkProfanity(String name, ProfanityChecker checker) {
        if (checker.containsProfanity(name)) {
            throw new ShoppingException(ErrorCode.PROFANITY_DETECTED);
        }
    }

    public Long id() {
        return id;
    }

    public String name() {
        return name;
    }

    public Integer price() {
        return price;
    }

    public String imageUrl() {
        return imageUrl;
    }
}
