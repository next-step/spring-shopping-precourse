package shopping.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Product {
    private static final Pattern INVALID_CHAR = Pattern.compile("[^가-힣a-zA-Z0-9 ()\\[\\]+\\-&/_]");
    private static final int MAX_NAME_LENGTH = 15;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private int price;
    private String imageUrl;

    private Product(String name, int price, String imageUrl) {
        this.name = name;
        this.price = price;
        this.imageUrl = imageUrl;
    }

    public static Product of(String name, int price, String imageUrl) {
        validateName(name);

        return new Product(name, price, imageUrl);
    }

    public void update(String name, int price, String imageUrl) {
        validateName(name);

        this.name = name;
        this.price = price;
        this.imageUrl = imageUrl;
    }

    private static void validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Product name is null or blank");
        }

        if (name.length() > MAX_NAME_LENGTH) {
            throw new IllegalArgumentException("Product name is too long (limit " + MAX_NAME_LENGTH+ ")");
        }

        Matcher matcher = INVALID_CHAR.matcher(name);
        if (matcher.find()) {
            throw new IllegalArgumentException("Product name included invalid characters : " + matcher.group());
        }
    }
}
