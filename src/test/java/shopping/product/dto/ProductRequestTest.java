package shopping.product.dto;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class ProductRequestTest {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @ParameterizedTest
    @ValueSource(strings = {"", "가나다라마바사아자차카타파하가나", "키보드!", "키보드  마우스"})
    void 잘못된_상품_이름이면_검증에_실패한다(String name) {
        ProductRequest request = new ProductRequest(name, 1000, "http://img.com/a.png");

        assertThat(validator.validate(request)).isNotEmpty();
    }

    @ParameterizedTest
    @ValueSource(strings = {"키보드", "Key (Black)", "[특가] 마우스+패드", "A&B/C_D-1"})
    void 올바른_상품_이름이면_검증에_성공한다(String name) {
        ProductRequest request = new ProductRequest(name, 1000, "http://img.com/a.png");

        assertThat(validator.validate(request)).isEmpty();
    }

    @Test
    void 가격이_음수이면_검증에_실패한다() {
        ProductRequest request = new ProductRequest("키보드", -1, "http://img.com/a.png");

        assertThat(validator.validate(request)).isNotEmpty();
    }
}
