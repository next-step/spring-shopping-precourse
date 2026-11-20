package shopping.product;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.assertThatCode;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class ImageUrlTest {
    @ParameterizedTest 
    @ValueSource(strings = {"https://example.com/image1.jpg", "https://example.com/image2.png", "https://example.com/image3.gif"})
    void http나_https로_시작하는_이미지_URL이어야한다(String value) {
        ImageUrl imageUrl = new ImageUrl(value);

        assertThat(imageUrl.getValue()).isEqualTo(value);
    }

    @ParameterizedTest 
    @NullAndEmptySource
    @ValueSource(strings = {" ", "  "})
    void 비어_있으면_예외가_발생한다(String value) {
        assertThatThrownBy(() -> new ImageUrl(value))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("이미지 URL은 비어있을 수 없습니다.");
    }

    @ParameterizedTest
    @ValueSource(strings = {"ftp://example.com/image.jpg", "example.com/image.gif", "http:/example.com/image.jpg", "https//exa mple.com/image.png", "https://"})
    void 올바른_URL이_아니면_예외가_발생한다(String value) {
        assertThatThrownBy(() -> new ImageUrl(value))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("이미지 URL은 http 또는 https로 시작해야 합니다.");
    }

    @Test
    void 길이가_255자이면_생성된다() {
        String value = "https://example.com/" + "a".repeat(235);
        assertThatCode(() -> new ImageUrl(value)).doesNotThrowAnyException();
    }

    @Test
    void 길이가_255자를_초과하면_예외가_발생한다() {
        String value = "https://example.com/" + "a".repeat(236);
        assertThatThrownBy(() -> new ImageUrl(value))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("이미지 URL은 최대 255자까지 입력할 수 있습니다.");
    }
}
