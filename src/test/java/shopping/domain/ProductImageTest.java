package shopping.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class ProductImageTest {
    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "   "})
    @DisplayName("null, 빈 값, 공백만 있는 URL은 예외가 발생한다")
    void createBlank(String url) {
        assertThatThrownBy(() -> new ProductImage(url))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "https://example.com/image.png",
            "https://example.com",
            "https://cdn.example.com:8443/a/b/c.jpg?size=100&type=thumb",
            "HTTPS://example.com/image.png"})
    @DisplayName("https URL은 생성할 수 있다")
    void createHttps(String url) {
        ProductImage image = new ProductImage(url);

        assertThat(image.url()).isEqualTo(url);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "http://example.com/image.png",
            "HTTP://example.com/image.png",
            "ftp://example.com/image.png",
            "file:///tmp/image.png",
            "javascript:alert(1)",
            "data:image/png;base64,AAAA",
            "//example.com/image.png",
            "example.com/image.png"})
    @DisplayName("https가 아닌 URL은 예외가 발생한다")
    void createNotHttps(String url) {
        assertThatThrownBy(() -> new ProductImage(url))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "https://",
            "https:///image.png",
            "https://exa mple.com/image.png",
            " https://example.com/image.png",
            "https://example.com/image.png ",
            "https://example.com/<script>"})
    @DisplayName("형식이 올바르지 않은 URL은 예외가 발생한다")
    void createMalformed(String url) {
        assertThatThrownBy(() -> new ProductImage(url))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
