package shopping.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class ProductNameTest {
    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"1234567890123456", "상품!", "상품@"})
    @DisplayName("상품 이름은 길이와 허용 문자 규칙을 지켜야 한다")
    void rejectInvalidProductName(String value) {
        assertThatThrownBy(() -> new ProductName(value))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @ParameterizedTest
    @ValueSource(strings = {"상", "123456789012345", "상품 ()[]+-&/_"})
    @DisplayName("상품 이름의 경계값과 허용 문자를 받아들인다")
    void createValidProductName(String value) {
        assertThat(new ProductName(value).value()).isEqualTo(value);
    }
}
