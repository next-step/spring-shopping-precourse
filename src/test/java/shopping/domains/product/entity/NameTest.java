package shopping.domains.product.entity;

import lombok.NonNull;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import shopping.domains.product.core.domain.entity.Name;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;
import static shopping.domains.product.test.fixture.ProductTestFixture.INVALID_NAMES;
import static shopping.domains.product.test.fixture.ProductTestFixture.NAMES;

class NameTest {
    @ParameterizedTest
    @DisplayName("상품 이름 생성자 테스트")
    @MethodSource("nameParameters")
    void constructorTest(@NonNull final String name) {
        // given
        assertThatNoException().isThrownBy(() -> new Name(name));
    }

    private static Stream<Arguments> nameParameters() {
        return NAMES.stream()
                .map(Arguments::of);
    }

    @ParameterizedTest
    @DisplayName("상품 이름 생성자 공백 테스트")
    @MethodSource("nameParameters")
    void constructorWhitespaceTest(@NonNull final String name) {
        // given
        final Name expected = new Name(name.trim());

        // when
        final Name actual = new Name(name);

        // then
        assertThat(actual).isEqualTo(expected);
    }

    @ParameterizedTest
    @DisplayName("상품 이름 생성자 예외 테스트")
    @MethodSource("invalidNameParameters")
    void constructorInvalidTest(@NonNull final String name) {
        assertThatIllegalArgumentException().isThrownBy(() -> new Name(name));
    }

    private static Stream<Arguments> invalidNameParameters() {
        return INVALID_NAMES.stream()
                .map(Arguments::of);
    }

    @Test
    @DisplayName("상품 이름 NPE 테스트")
    void constructorNPETest() {
        assertThatNullPointerException().isThrownBy(() -> new Name(null));
    }
}
