package shopping.product.dto;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

class ProductRequestTest {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    @DisplayName("이미지는 파일이 아닌 URL 문자열로 입력받는다")
    void inputImageUrl() {
        ProductRequest request =
                new ProductRequest("캐러멜 팝콘", 5_000L, "https://example.com/popcorn.jpg");

        assertThat(request.imageUrl()).isEqualTo("https://example.com/popcorn.jpg");
    }

    @Test
    @DisplayName("상품 이름은 공백을 포함해 15자까지 입력할 수 있다")
    void nameWithinMaxLength() {
        ProductRequest request =
                new ProductRequest("1234567890 1234", 5_000L, "https://example.com/product.jpg");

        Set<ConstraintViolation<ProductRequest>> violations = validator.validate(request);

        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("상품 이름이 15자를 초과하면 검증에 실패한다")
    void nameOverMaxLength() {
        ProductRequest request =
                new ProductRequest("1234567890123456", 5_000L, "https://example.com/product.jpg");

        assertThat(messagesOf(request)).contains("상품 이름은 15자를 초과할 수 없습니다.");
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "   "})
    @DisplayName("상품 이름이 비어 있으면 검증에 실패한다")
    void blankName(String name) {
        ProductRequest request =
                new ProductRequest(name, 5_000L, "https://example.com/product.jpg");

        assertThat(messagesOf(request)).contains("상품 이름은 비어 있을 수 없습니다.");
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "상품(1)", "상품[1]", "상품+1", "상품-1", "상품&1", "상품/1", "상품_1"
    })
    @DisplayName("상품 이름에 허용된 특수 문자를 사용할 수 있다")
    void allowedSpecialCharacter(String name) {
        ProductRequest request =
                new ProductRequest(name, 5_000L, "https://example.com/product.jpg");

        assertThat(validator.validate(request)).isEmpty();
    }

    @ParameterizedTest
    @ValueSource(strings = {"상품!", "상품@", "상품#", "상품%", "상품*"})
    @DisplayName("상품 이름에 허용되지 않은 특수 문자가 있으면 검증에 실패한다")
    void notAllowedSpecialCharacter(String name) {
        ProductRequest request =
                new ProductRequest(name, 5_000L, "https://example.com/product.jpg");

        assertThat(messagesOf(request))
                .contains("상품 이름에 허용되지 않은 특수 문자가 포함되어 있습니다.");
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(longs = {-1L})
    @DisplayName("상품 가격이 비어 있거나 음수면 검증에 실패한다")
    void invalidPrice(Long price) {
        ProductRequest request =
                new ProductRequest("상품", price, "https://example.com/product.jpg");

        assertThat(validator.validate(request)).isNotEmpty();
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "   "})
    @DisplayName("이미지 URL이 비어 있으면 검증에 실패한다")
    void blankImageUrl(String imageUrl) {
        ProductRequest request = new ProductRequest("상품", 5_000L, imageUrl);

        assertThat(messagesOf(request)).contains("이미지 URL은 비어 있을 수 없습니다.");
    }

    private Set<String> messagesOf(ProductRequest request) {
        return validator.validate(request).stream()
                .map(ConstraintViolation::getMessage)
                .collect(java.util.stream.Collectors.toSet());
    }
}
