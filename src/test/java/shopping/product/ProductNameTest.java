package shopping.product;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class ProductNameTest {
    @Test
    @DisplayName("정상 이름은 만들어진다")
    void createsValidName() {
        ProductName name = new ProductName("딸기");
        assertThat(name.getValue()).isEqualTo("딸기");
    }

    @Test
    @DisplayName("빈 이름이면 예외가 발생한다")
    void throwsWhenBlank() {
        assertThatThrownBy(() -> new ProductName(" "))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("15글자를 넘으면 예외가 발생한다")
    void throwsWhenTooLong() {
        assertThatThrownBy(() -> new ProductName("가나다라마바사아자차카타파하ab"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @ParameterizedTest
    @ValueSource(strings = {"쌀국수!", "딸기@", "사과#", "포도$", "귤*", "배%"})
    @DisplayName("허용되지 않은 특수문자를 사용할 경우 예외가 발생한다")
    void throwsWhenInvalidChar(String name) {
        assertThatThrownBy(() -> new ProductName(name))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("허용된 특수 문자는 통과한다")
    void createsNameWithAllowedChars() {
        ProductName name = new ProductName("(대)[딸기]+-&/_");
        assertThat(name.getValue()).isEqualTo("(대)[딸기]+-&/_");
    }
}
