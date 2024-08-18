package shopping.domains.user.core.domain.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import shopping.domains.product.core.domain.entity.Product;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;
import static shopping.domains.product.test.fixture.ProductTestFixture.PRODUCT;
import static shopping.domains.user.test.fixture.UserTestFixture.USER;

class WishlistTest {
    @Test
    @DisplayName("위시리스트 생성자 테스트")
    void constructorTest() {
        assertThatNoException().isThrownBy(() -> new Wishlist(USER, PRODUCT));
    }

    @ParameterizedTest
    @DisplayName("위시리스트 생성자 NPE 테스트")
    @MethodSource("constructorNullParameters")
    void constructorNPETest(
            final User user,
            final Product product
    ) {
        assertThatNullPointerException().isThrownBy(() -> Wishlist.builder()
                .user(user)
                .product(product)
                .build());
    }

    private static Stream<Arguments> constructorNullParameters() {
        // given
        return Stream.of(
                Arguments.of(null, PRODUCT),
                Arguments.of(USER, null)
        );
    }
}
