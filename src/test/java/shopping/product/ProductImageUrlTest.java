package shopping.product;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class ProductImageUrlTest {

    @Test
    @DisplayName("정상 사진 주소는 만들어진다")
    void createsValidImageUrl() {
        ProductImageUrl productImageUrl = new ProductImageUrl("http://example.com/example.png");
        assertThat(productImageUrl.getValue()).isEqualTo("http://example.com/example.png");
    }

    @Test
    @DisplayName("빈 주소면 예외가 발생한다")
    void throwsWhenBlank() {
        assertThatThrownBy(() -> new ProductImageUrl(" "))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("주소가 없으면(null) 예외가 발생한다")
    void throwsWhenNull() {
        assertThatThrownBy(() -> new ProductImageUrl(null))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
