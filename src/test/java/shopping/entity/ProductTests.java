package shopping.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.*;

public class ProductTests {

    @DisplayName("상품 이름이 null이거나 비어 있거나 공백 뿐이면 예외가 발생한다")
    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "   "})
    void productNameIsBlank(String name) {
        int price = 0;
        String imageUrl = "https://api.dicebear.com/10.x/waves/svg";

        assertThatThrownBy(() -> Product.of(name, price, imageUrl))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("상품 이름이 정확히 15자이면 생성할 수 있다")
    @ParameterizedTest
    @ValueSource(strings = {"aaaaaaaaaaaaaaa", "가가가가가가가가가가가가가가가"})
    void nameWithMaxLengthIsAllowed(String name) {
        int price = 0;
        String imageUrl = "https://api.dicebear.com/10.x/waves/svg";

        assertThat(name).hasSize(15);
        assertThatCode(() -> Product.of(name, price, imageUrl))
                .doesNotThrowAnyException();
    }

    @DisplayName("상품 이름이 15자를 초과하면 예외가 발생한다")
    @Test
    void nameExceedsMaxLength() {
        String name = "a".repeat(16);
        int price = 0;
        String imageUrl = "https://api.dicebear.com/10.x/waves/svg";

        assertThatThrownBy(() -> Product.of(name, price, imageUrl))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("상품 이름에 한글, 영문, 숫자, 공백은 사용할 수 있다")
    @ParameterizedTest
    @ValueSource(strings = {"사과", "Apple", "123", "사과 Apple 123"})
    void nameContainsBasicCharacters(String name) {
        int price = 0;
        String imageUrl = "https://api.dicebear.com/10.x/waves/svg";

        assertThatCode(() -> Product.of(name, price, imageUrl))
                .doesNotThrowAnyException();
    }

    @DisplayName("상품 이름에 허용된 특수문자는 사용할 수 있다")
    @ParameterizedTest
    @ValueSource(strings = {"사과(빨강)", "상품 [1]", "A+B", "a-b", "A&B", "A/B", "a_b"})
    void nameContainsAllowedSpecialCharacter(String name) {
        int price = 0;
        String imageUrl = "https://api.dicebear.com/10.x/waves/svg";

        assertThatCode(() -> Product.of(name, price, imageUrl))
                .doesNotThrowAnyException();
    }

    @DisplayName("상품을 수정하면 이름, 가격, 이미지 URL이 모두 변경된다")
    @Test
    void updateChangesAllFields() {
        Product product = Product.of("치킨", 20000, "https://example.com/a.svg");

        product.update("피자", 25000, "https://example.com/b.svg");

        assertThat(product.getName()).isEqualTo("피자");
        assertThat(product.getPrice()).isEqualTo(25000);
        assertThat(product.getImageUrl()).isEqualTo("https://example.com/b.svg");
    }

    @DisplayName("상품 수정 시 이름이 유효하지 않으면 예외가 발생하고 기존 값은 유지된다")
    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "상품!", "aaaaaaaaaaaaaaaa"})
    void updateWithInvalidName(String name) {
        Product product = Product.of("치킨", 20000, "https://example.com/a.svg");

        assertThatThrownBy(() -> product.update(name, 25000, "https://example.com/b.svg"))
                .isInstanceOf(IllegalArgumentException.class);
        assertThat(product.getName()).isEqualTo("치킨");
        assertThat(product.getPrice()).isEqualTo(20000);
    }

    @DisplayName("상품 이름에 허용되지 않은 특수문자가 있으면 예외가 발생한다")
    @ParameterizedTest
    @ValueSource(strings = {"상품!", "상품@", "상품#", "상품*", "상품😀"})
    void nameContainsInvalidSpecialCharacter(String name) {
        int price = 0;
        String imageUrl = "https://api.dicebear.com/10.x/waves/svg";

        assertThatThrownBy(() -> Product.of(name, price, imageUrl))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
