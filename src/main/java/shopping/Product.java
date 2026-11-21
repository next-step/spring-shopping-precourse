package shopping;

import org.jspecify.annotations.NullMarked;

import java.util.Objects;
import java.util.regex.Pattern;

@NullMarked
public final class Product {
    private static final Pattern VALID_PATTERN =
            Pattern.compile("^[a-zA-Z0-9가-힣\\s()\\[\\]+&/_-]+$");
    public static final int MAX_NAME_LENGTH = 15;

    private final Long id;
    private final String name;
    private final long price;
    private final String url;

    public Product(Long id, String name, long price, String url, ProductNameFilter productNameFilter) {
        validateName(name);
        if (price < 0) {
            throw new IllegalArgumentException("상품 가격은 0원 이상이어야 합니다");
        }
        if (productNameFilter.profanity(name)) {
            throw new IllegalArgumentException("상품 이름에 비속어가 포함되어 있습니다");
        }
        this.id = id;
        this.name = name;
        this.price = price;
        this.url = url;
    }

    private static void validateName(String name) {
        if (name.isBlank()) {
            throw new IllegalArgumentException();
        }
        if (name.length() > MAX_NAME_LENGTH) {
            throw new IllegalArgumentException();
        }
        if (!VALID_PATTERN.matcher(name).matches()) {
            throw new IllegalArgumentException();
        }
    }

    public Long id() {
        return id;
    }

    public String name() {
        return name;
    }

    public long price() {
        return price;
    }

    public String url() {
        return url;
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof Product product)) {
            return false;
        }
        return price == product.price
                && Objects.equals(id, product.id)
                && Objects.equals(name, product.name)
                && Objects.equals(url, product.url);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, price, url);
    }
}
