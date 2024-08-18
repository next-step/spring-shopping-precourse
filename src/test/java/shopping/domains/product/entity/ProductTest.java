package shopping.domains.product.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import shopping.domains.product.core.domain.entity.Image;
import shopping.domains.product.core.domain.entity.Name;
import shopping.domains.product.core.domain.entity.Price;
import shopping.domains.product.core.domain.entity.Product;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;
import static shopping.domains.product.test.fixture.ProductTestFixture.*;

class ProductTest {
    @Test
    @DisplayName("상품 생성자 테스트")
    void constructorTest() {
        // given
        final Name name = new Name(NAMES.get(0));
        final Price price = new Price(PRICES.get(0));
        final Image image = new Image(IMAGE_URLS.get(0));

        // when & then
        assertThatNoException().isThrownBy(() -> new Product(name, price, image));
    }

    @ParameterizedTest
    @DisplayName("상품 생성자 NPE 테스트")
    @MethodSource("constructorNullParameters")
    void constructorNPETest(
            final Name name,
            final Price price,
            final Image image
    ) {
        assertThatNullPointerException().isThrownBy(() -> Product.builder()
                .name(name)
                .price(price)
                .image(image)
                .build());
    }

    private static Stream<Arguments> constructorNullParameters() {
        // given
        final Name name = new Name(NAMES.get(0));
        final Price price = new Price(PRICES.get(0));
        final Image image = new Image(IMAGE_URLS.get(0));

        return Stream.of(
                Arguments.of(null, price, image),
                Arguments.of(name, null, image),
                Arguments.of(name, price, null)
        );
    }
}
