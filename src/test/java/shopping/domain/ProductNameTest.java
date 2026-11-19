package shopping.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class ProductNameTest {
    @Test
    @DisplayName("공백 포함 15자 이하의 영문 이름은 생성할 수 있다")
    void createWithMaxLength() {
        String value = "abcde fghij klm";

        ProductName name = new ProductName(value);

        assertThat(name.value()).isEqualTo(value);
    }

    @Test
    @DisplayName("16자 이상의 이름은 예외가 발생한다")
    void createOverMaxLength() {
        String value = "abcde fghij klmn";

        assertThatThrownBy(() -> new ProductName(value))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "   "})
    @DisplayName("null, 빈 값, 공백만 있는 이름은 예외가 발생한다")
    void createBlank(String value) {
        assertThatThrownBy(() -> new ProductName(value))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @ParameterizedTest
    @ValueSource(strings = {"(", ")", "[", "]", "+", "-", "&", "/", "_"})
    @DisplayName("허용된 특수문자는 이름에 사용할 수 있다")
    void createWithAllowedSpecialCharacter(String special) {
        String value = "Item" + special + "A1";

        ProductName name = new ProductName(value);

        assertThat(name.value()).isEqualTo(value);
    }

    @ParameterizedTest
    @ValueSource(strings = {"!", "@", "#", "$", "%", "^", "*", "=", "{", "}", "<", ">", "?",
            ".", ",", ":", ";", "'", "\"", "\\", "|", "~", "`"})
    @DisplayName("허용되지 않은 특수문자가 있으면 예외가 발생한다")
    void createWithNotAllowedSpecialCharacter(String special) {
        String value = "Item" + special;

        assertThatThrownBy(() -> new ProductName(value))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @ParameterizedTest
    @ValueSource(strings = {"상품", "ㅅㅂ", "商品", "あ", "é", "Ａ", "😀", "❤️", "1️⃣", "🇰🇷"})
    @DisplayName("영문과 숫자가 아닌 글자(한글, 한자, 이모지 등)가 있으면 예외가 발생한다")
    void createWithNonEnglish(String text) {
        String value = "Item" + text;

        assertThatThrownBy(() -> new ProductName(value))
                .isInstanceOf(IllegalArgumentException.class);
    }

}
