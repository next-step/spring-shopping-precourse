package shopping.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class ProductIdTest {
    @ParameterizedTest
    @ValueSource(longs = {1, 100, Long.MAX_VALUE})
    @DisplayName("1 이상의 값으로 생성할 수 있다")
    void createPositive(long value) {
        ProductId id = new ProductId(value);

        assertThat(id.value()).isEqualTo(value);
    }

    @ParameterizedTest
    @ValueSource(longs = {0, -1, Long.MIN_VALUE})
    @DisplayName("0 이하의 값은 예외가 발생한다")
    void createNotPositive(long value) {
        assertThatThrownBy(() -> new ProductId(value))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("값이 같으면 같은 ID이다")
    void equality() {
        assertThat(new ProductId(1)).isEqualTo(new ProductId(1));
        assertThat(new ProductId(1)).hasSameHashCodeAs(new ProductId(1));
        assertThat(new ProductId(1)).isNotEqualTo(new ProductId(2));
    }
}
