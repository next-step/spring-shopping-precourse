package shopping.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;

@Entity
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Pattern(regexp = "^[a-zA-Z0-9 ()\\[\\]+\\-_/]*$", message = "Invalid characters in product name")
    private String name;

    @Positive
    private Integer price;

    @Pattern(regexp = "^https?://.*", message = "Invalid image URL")
    private String imageUrl;
    private static final String REGEX = "[^a-zA-Z0-9()\\[\\]+\\-&/_]";

    public Product() {}

    public Product(String name) {
        this(name, null, null);
    }

    public Product(String name, Integer price) {
        this(name, price, null);
    }

    public Product(String name, Integer price, String imageUrl) {
        validateName(name);
        this.name = name;
        this.price = price;
        this.imageUrl = imageUrl;
    }

    private static void validateName(String name) {
        if (name == null || 15 < name.length()) {
            throw new IllegalArgumentException("Product name must have at least 15 characters");
        }
        if (name.matches(".*" + REGEX + ".*")) {
            throw new IllegalArgumentException("Product name must not contain special characters");
        }
    }

    public String getName() {
        return name;
    }

    public Integer getPrice() {
        return price;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setName(String name) {
        validateName(name);
        this.name = name;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
