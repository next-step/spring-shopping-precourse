package shopping.domains.product.entity;

import lombok.NonNull;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import shopping.domains.product.core.domain.entity.Image;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;
import static shopping.domains.product.test.fixture.ProductTestFixture.IMAGE_URLS;
import static shopping.domains.product.test.fixture.ProductTestFixture.INVALID_IMAGE_URLS;

class ImageTest {
    @ParameterizedTest
    @DisplayName("이미지 생성자 테스트")
    @MethodSource("imageUrlParameters")
    void constructorTest(@NonNull final String imageUrl) {
        assertThatNoException().isThrownBy(() -> new Image(imageUrl));
    }

    private static Stream<Arguments> imageUrlParameters() {
        return IMAGE_URLS.stream()
                .map(Arguments::of);
    }

    @ParameterizedTest
    @DisplayName("이미지 생성자 공백 테스트")
    @MethodSource("imageUrlParameters")
    void constructorWhitespaceTest(@NonNull final String imageUrl) {
        // given
        final Image expected = new Image(imageUrl.trim());

        // when
        final Image actual = new Image(imageUrl);

        // then
        assertThat(actual).isEqualTo(expected);
    }

    @ParameterizedTest
    @DisplayName("이미지 생성자 예외 테스트")
    @MethodSource("invalidImageUrlParameters")
    void constructorExceptionTest(@NonNull final String imageUrl) {
        assertThatIllegalArgumentException().isThrownBy(() -> new Image(imageUrl));
    }

    private static Stream<Arguments> invalidImageUrlParameters() {
        return INVALID_IMAGE_URLS.stream()
                .map(Arguments::of);
    }

    @Test
    @DisplayName("상품 이미지 NPE 테스트")
    void constructorNPETest() {
        assertThatNullPointerException().isThrownBy(() -> new Image(null));
    }
}
