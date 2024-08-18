package shopping.domains.product.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import shopping.domains.product.core.domain.entity.Price;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static shopping.domains.product.test.fixture.ProductTestFixture.INVALID_PRICES;
import static shopping.domains.product.test.fixture.ProductTestFixture.PRICES;

class PriceTest {
    @ParameterizedTest
    @DisplayName("상품 가격 생성자 테스트")
    @MethodSource("priceParameters")
    void constructorTest(final long price) {
        assertThatNoException().isThrownBy(() -> new Price(price));
    }

    private static Stream<Arguments> priceParameters() {
        return PRICES.stream()
                .map(Arguments::of);
    }

    @ParameterizedTest
    @DisplayName("상품 가격 생성자 예외 테스트")
    @MethodSource("invalidPriceParameters")
    void constructorExceptionTest(final long price) {
        assertThatIllegalArgumentException().isThrownBy(() -> new Price(price));
    }

    private static Stream<Arguments> invalidPriceParameters() {
        return INVALID_PRICES.stream()
                .map(Arguments::of);
    }
}
