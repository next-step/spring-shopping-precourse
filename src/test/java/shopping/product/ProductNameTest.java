package shopping.product;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class ProductNameTest {

    @Test
    @DisplayName("이름이 있으면 상품 이름을 만든다")
    void createName() {
        ProductName name = new ProductName("사과");

        assertThat(name.value()).isEqualTo("사과");
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "   "})
    @DisplayName("상품 이름이 비어 있거나 공백만 포함하면 거부한다")
    void rejectBlankName(String value) {
        assertThatThrownBy(() -> new ProductName(value))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("상품 이름은 비어 있을 수 없습니다.");
    }

    @ParameterizedTest
    @ValueSource(strings = {"가나다라마바사아자차카타파하가", "a b c d e f g h"})
    @DisplayName("상품 이름은 공백을 포함하여 최대 15자까지 허용한다")
    void acceptNameUpToMaxLength(String value) {
        ProductName name = new ProductName(value);

        assertThat(name.value()).isEqualTo(value);
    }

    @ParameterizedTest
    @ValueSource(strings = {"가나다라마바사아자차카타파하가나", "abcdefghijklmnop"})
    @DisplayName("상품 이름이 15자를 초과하면 거부한다")
    void rejectNameOverMaxLength(String value) {
        assertThatThrownBy(() -> new ProductName(value))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("상품 이름은 공백 포함 최대 15자까지 입력할 수 있습니다.");
    }

    @ParameterizedTest
    @ValueSource(strings = {"사과(대)", "[특가] 사과", "사과+배", "사과-배", "사과&배", "사과/배", "사과_배",
            "Apple 2kg"})
    @DisplayName("상품 이름에 ( ) [ ] + - & / _ 특수문자를 허용한다")
    void acceptAllowedCharacters(String value) {
        ProductName name = new ProductName(value);

        assertThat(name.value()).isEqualTo(value);
    }

    @ParameterizedTest
    @ValueSource(strings = {"사과!", "사과@배", "사과#", "사과*", "사과.배", "사과😀"})
    @DisplayName("허용되지 않은 특수문자가 포함되면 거부한다")
    void rejectNotAllowedCharacters(String value) {
        assertThatThrownBy(() -> new ProductName(value))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("상품 이름에는 ( ), [ ], +, -, &, /, _ 외의 특수문자를 사용할 수 없습니다.");
    }
}
