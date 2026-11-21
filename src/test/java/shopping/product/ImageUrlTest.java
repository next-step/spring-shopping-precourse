package shopping.product;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class ImageUrlTest {

    @Test
    @DisplayName("이미지 URL이 있으면 허용한다")
    void acceptImageUrl() {
        ImageUrl imageUrl = new ImageUrl("https://example.com/apple.png");

        assertThat(imageUrl.value()).isEqualTo("https://example.com/apple.png");
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "   "})
    @DisplayName("상품 이미지 URL이 비어 있으면 거부한다")
    void rejectBlankImageUrl(String value) {
        assertThatThrownBy(() -> new ImageUrl(value))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("상품 이미지 URL은 비어 있을 수 없습니다.");
    }
}
