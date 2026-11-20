package shopping;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.util.regex.Pattern;

@Entity
public class Product {

    private static final int MAX_NAME_LENGTH = 15;
    /*
     * \p{L}: 유니코드 문자, \p{N}: 유니코드 숫자
     * (): 소괄호, []: 대괄호, + - & / _: 허용된 특수 문자
     * \\: Java 문자열에서 백슬래시(\)를 표현하는 이스케이프 시퀀스
     * +: 문자 집합이 1회 이상 반복됨
     */
    private static final Pattern VALID_NAME_PATTERN = Pattern.compile("[\\p{L}\\p{N} ()\\[\\]+&/_-]+");
    private static final long MAX_PRICE = 1_000_000L;
    private static final String HTTP_PREFIX = "http://";
    private static final String HTTPS_PREFIX = "https://";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private long price;
    private String imageUrl;

    protected Product() {
    }

    public Product(String name, long price, String imageUrl) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("상품 이름은 비어 있을 수 없습니다.");
        }
        if (name.length() > MAX_NAME_LENGTH) {
            throw new IllegalArgumentException("상품 이름은 15자를 초과할 수 없습니다.");
        }
        if (!VALID_NAME_PATTERN.matcher(name).matches()) {
            throw new IllegalArgumentException("상품 이름에 허용되지 않은 문자가 포함되어 있습니다.");
        }
        if (price <= 0 || price > MAX_PRICE) {
            throw new IllegalArgumentException("상품 가격은 1원 이상 1,000,000원 이하이어야 합니다.");
        }
        if (imageUrl == null || !(imageUrl.startsWith(HTTP_PREFIX) || imageUrl.startsWith(HTTPS_PREFIX))) {
            throw new IllegalArgumentException("이미지 URL은 http:// 혹은 https://로 시작해야 합니다.");
        }
        this.name = name;
        this.price = price;
        this.imageUrl = imageUrl;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public long getPrice() {
        return price;
    }

    public String getImageUrl() {
        return imageUrl;
    }

}
