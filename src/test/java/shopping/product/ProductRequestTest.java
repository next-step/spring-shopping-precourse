package shopping.product;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.Set;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class ProductRequestTest {
    private static final ValidatorFactory FACTORY =
            Validation.buildDefaultValidatorFactory();
    private static final Validator VALIDATOR = FACTORY.getValidator();

    @AfterAll
    static void closeFactory() {
        FACTORY.close();
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"가나다라마바사아자차카타파하가나", "상품!", "상품\t", "상품\n"})
    @DisplayName("상품 이름은 길이와 허용 문자 규칙을 지켜야 한다")
    void rejectInvalidName(String name) {
        assertThat(validateName(name)).isNotEmpty();
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 15})
    @DisplayName("상품 이름은 1자와 15자를 사용할 수 있다")
    void acceptNameAtLengthBoundary(int length) {
        assertThat(VALIDATOR.validate(productRequest("가".repeat(length)))).isEmpty();
    }

    @Test
    @DisplayName("상품 이름의 길이를 검사할 때 공백도 포함한다")
    void countSpacesInNameLength() {
        assertThat(validateName("가".repeat(14) + " ")).isEmpty();
        assertThat(validateName("가".repeat(15) + " ")).isNotEmpty();
    }

    @ParameterizedTest
    @ValueSource(strings = {"상품", "Product", "123", "상품 이름"})
    @DisplayName("상품 이름은 한글 영문 숫자와 문자 사이 공백을 사용할 수 있다")
    void acceptLettersNumbersAndSpace(String name) {
        assertThat(VALIDATOR.validate(productRequest(name))).isEmpty();
    }

    @ParameterizedTest
    @ValueSource(strings = {"(", ")", "[", "]", "+", "-", "&", "/", "_"})
    @DisplayName("상품 이름에 지정된 특수문자를 사용할 수 있다")
    void acceptAllowedSpecialCharacter(String character) {
        assertThat(VALIDATOR.validate(productRequest("상품" + character))).isEmpty();
    }

    @ParameterizedTest
    @ValueSource(strings = {"!", "@", "#", "*", "\\"})
    @DisplayName("상품 이름에 허용되지 않은 특수문자를 사용할 수 없다")
    void rejectDisallowedSpecialCharacter(String character) {
        assertThat(validateName("상품" + character)).isNotEmpty();
    }

    @Test
    @DisplayName("상품 가격은 null일 수 없다")
    void rejectNullPrice() {
        ProductRequest request = new ProductRequest("상품", null, "image-url");

        assertThat(VALIDATOR.validateProperty(request, "price")).isNotEmpty();
    }

    @Test
    @DisplayName("상품 이미지 URL은 null일 수 없다")
    void rejectNullImageUrl() {
        ProductRequest request = new ProductRequest("상품", 1_000L, null);

        assertThat(VALIDATOR.validateProperty(request, "imageUrl")).isNotEmpty();
    }

    @Test
    @DisplayName("유효한 상품 입력은 검증을 통과한다")
    void acceptValidProductRequest() {
        assertThat(VALIDATOR.validate(productRequest("상품 이름"))).isEmpty();
    }

    private ProductRequest productRequest(String name) {
        return new ProductRequest(name, 1_000L, "https://example.com/product.png");
    }

    private Set<ConstraintViolation<ProductRequest>> validateName(String name) {
        return VALIDATOR.validateProperty(productRequest(name), "name");
    }
}
