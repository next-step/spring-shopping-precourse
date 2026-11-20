package shopping.product;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;


class ProductNameTest {
    // Test cases for ProductName class

    @Test
    void 상품_이름을_생성할_수_있다() {
        String value = "Apple";
        ProductName productName = new ProductName(value);
        assertThat(productName.getValue()).isEqualTo(value);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource (strings = {" ", "   "})
    void 상품_이름이_비어있으면_예외가_발생한다(String value) {
        assertThatThrownBy(() -> new ProductName(value))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("상품 이름은 비어 있을 수 없습니다.");
    }

    @Test
    void 상품_이름이_15자_이하라면_생성된다() {
        String value = "가".repeat(15);
        assertThatCode(() -> new ProductName(value)).doesNotThrowAnyException();
    }

    @Test
    void 상품_이름이_15자를_초과하면_예외가_발생한다() {
        String value = "가".repeat(16);
        assertThatThrownBy(() -> new ProductName(value))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("상품 이름은 최대 15자까지 입력할 수 있습니다.");
    }

    @Test
    void 공백도_글자_수에_포함된다() {
        String value = "가".repeat(8) + " " + "나".repeat(7);
        assertThatThrownBy(() -> new ProductName(value))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("상품 이름은 최대 15자까지 입력할 수 있습니다.");
    }

    @ParameterizedTest
    @ValueSource (strings = {"콜라(L)", "[특가]과자", "과자+음료", "A-B", "A&B", "A/B", "A_B", "Coffee 2"})
    void 허용된_특수문자는_사용할_수_있다(String value) {
        assertThatCode(() -> new ProductName(value)).doesNotThrowAnyException();
    }

    @ParameterizedTest 
    @ValueSource (strings = {"커피!", "콜라@", "과자#", "우유*", "빵.", "떡,", "차~", "물?", "커피☕"})
    void 허용되지_않는_특수문자는_사용할_수_없다(String value) {
        assertThatThrownBy(() -> new ProductName(value))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("허용되지 않은 특수 문자가 포함되어 있습니다.");
    }

    @Test
    void 값이_같으면_같은_객체로_본다() {
        ProductName name1 = new ProductName("아이스 아메리카노");
        ProductName name2 = new ProductName("아이스 아메리카노");

        assertThat(name1).isEqualTo(name2);
    }
}