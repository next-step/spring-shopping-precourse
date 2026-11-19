package shopping.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ProductTest {
    private static final ProfanityChecker NO_PROFANITY = text -> false;

    private static ProductName name(String value) {
        return new ProductName(value, NO_PROFANITY);
    }

    private static ProductPrice price(long value) {
        return new ProductPrice(value);
    }

    private static ProductImage image(String url) {
        return new ProductImage(url);
    }

    private static Product product() {
        return new Product(name("Galaxy S24"), price(1000), image("https://example.com/a.png"));
    }

    @Test
    @DisplayName("이름, 가격, 이미지로 상품을 생성할 수 있다")
    void create() {
        Product product = product();

        assertThat(product.name().value()).isEqualTo("Galaxy S24");
        assertThat(product.price().value()).isEqualTo(1000);
        assertThat(product.image().url()).isEqualTo("https://example.com/a.png");
    }

    @Test
    @DisplayName("이름이 규칙을 어기면 상품을 만들 수 없다")
    void createWithInvalidName() {
        assertThatThrownBy(() -> new Product(name("bad!"), price(1000), image("https://a.com")))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("가격이 규칙을 어기면 상품을 만들 수 없다")
    void createWithInvalidPrice() {
        assertThatThrownBy(() -> new Product(name("Item"), price(-1), image("https://a.com")))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("이미지가 규칙을 어기면 상품을 만들 수 없다")
    void createWithInvalidImage() {
        assertThatThrownBy(() -> new Product(name("Item"), price(1), image("http://a.com")))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("이름, 가격, 이미지 중 하나라도 null이면 상품을 만들 수 없다")
    void createWithNull() {
        ProductName name = name("Item");
        ProductPrice price = price(1);
        ProductImage image = image("https://a.com");

        assertThatThrownBy(() -> new Product(null, price, image))
                .isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> new Product(name, null, image))
                .isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> new Product(name, price, null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("상품을 수정하면 이름, 가격, 이미지가 모두 바뀐다")
    void update() {
        Product product = product();

        product.update(name("Pixel 9"), price(2000), image("https://b.com/b.png"));

        assertThat(product.name().value()).isEqualTo("Pixel 9");
        assertThat(product.price().value()).isEqualTo(2000);
        assertThat(product.image().url()).isEqualTo("https://b.com/b.png");
    }

    @Test
    @DisplayName("수정할 값이 규칙을 어기면 예외가 발생하고 기존 값은 유지된다")
    void updateWithInvalidValue() {
        Product product = product();

        assertThatThrownBy(() ->
                product.update(name("Pixel 9"), price(-1), image("https://b.com/b.png")))
                .isInstanceOf(IllegalArgumentException.class);

        assertThat(product.name().value()).isEqualTo("Galaxy S24");
        assertThat(product.price().value()).isEqualTo(1000);
    }

    @Test
    @DisplayName("수정할 값이 null이면 예외가 발생하고 기존 값은 유지된다")
    void updateWithNull() {
        Product product = product();
        ProductName newName = name("Pixel 9");
        ProductImage newImage = image("https://b.com/b.png");

        assertThatThrownBy(() -> product.update(newName, null, newImage))
                .isInstanceOf(NullPointerException.class);

        assertThat(product.name().value()).isEqualTo("Galaxy S24");
        assertThat(product.image().url()).isEqualTo("https://example.com/a.png");
    }

    @Test
    @DisplayName("ID 없이 만든 상품(아직 저장 전)은 ID가 없다")
    void createWithoutId() {
        assertThat(product().id()).isEmpty();
    }

    @Test
    @DisplayName("ID와 함께 상품을 생성할 수 있다")
    void createWithId() {
        Product product = new Product(new ProductId(7), name("Item"), price(1),
                image("https://a.com"));

        assertThat(product.id()).contains(new ProductId(7));
        assertThat(product.name().value()).isEqualTo("Item");
    }

    @Test
    @DisplayName("ID를 null로 넘기면 상품을 만들 수 없다")
    void createWithNullId() {
        assertThatThrownBy(() -> new Product(null, name("Item"), price(1), image("https://a.com")))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("상품을 수정해도 ID는 바뀌지 않는다")
    void updateKeepsId() {
        Product product = new Product(new ProductId(7), name("Item"), price(1),
                image("https://a.com"));

        product.update(name("Pixel 9"), price(2000), image("https://b.com"));

        assertThat(product.id()).contains(new ProductId(7));
    }
}
