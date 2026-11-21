package shopping.product.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import shopping.common.error.ErrorCode;
import shopping.common.error.ShoppingException;

@DisplayName("상품")
class ProductTest {

    @DisplayName("허용된 문자로 구성된 상품 이름은 생성할 수 있다")
    @Test
    void acceptsAllowedNameCharactersAtMaximumLength() {
        assertThatCode(() -> product("상품()[]+-&/_ 123", 1)).doesNotThrowAnyException();
    }

    @DisplayName("누락되거나 길이와 문자 규칙을 어긴 상품 이름은 거절한다")
    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "상품!", "가나다라마바사아자차카타파하가나"})
    void rejectsInvalidNames(String name) {
        assertThatIllegalArgumentException().isThrownBy(() -> product(name, 1));
    }

    @DisplayName("상품 가격은 1원과 정수 최댓값을 허용한다")
    @Test
    void acceptsPositiveIntegerPriceBoundaries() {
        assertThatCode(() -> product("상품", 1)).doesNotThrowAnyException();
        assertThatCode(() -> product("상품", Integer.MAX_VALUE)).doesNotThrowAnyException();
    }

    @DisplayName("가격이 누락되거나 0원 이하이면 거절한다")
    @ParameterizedTest
    @NullSource
    @ValueSource(ints = {0, -1})
    void rejectsMissingOrNonPositivePrice(Integer price) {
        assertThatIllegalArgumentException().isThrownBy(() -> product("상품", price));
    }

    @DisplayName("호스트가 있는 HTTP와 HTTPS 이미지 URL을 허용한다")
    @ParameterizedTest
    @ValueSource(strings = {"http://example.com/image.png", "https://example.com/image.png"})
    void acceptsHttpAndHttpsImageUrls(String url) {
        assertThatCode(() -> new Product("상품", 1, url)).doesNotThrowAnyException();
    }

    @DisplayName("누락되거나 호스트와 프로토콜이 올바르지 않은 이미지 URL은 거절한다")
    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"image.png", "http://", "ftp://example.com/a"})
    void requiresHttpUrlWithHost(String url) {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> new Product("상품", 1, url));
    }

    @DisplayName("비속어가 없는 상품 이름만 검사를 통과한다")
    @Test
    void rejectsProfanityButAcceptsCleanName() {
        Product product = product("상품", 1);

        assertThatCode(() -> product.checkProfanity(text -> false)).doesNotThrowAnyException();
        assertThatThrownBy(() -> product.checkProfanity(text -> true))
                .isInstanceOfSatisfying(ShoppingException.class,
                        error -> assertThat(error.code()).isEqualTo(ErrorCode.PROFANITY_DETECTED));
    }

    @DisplayName("상품 수정 시 이름과 가격과 이미지 URL을 함께 변경한다")
    @Test
    void replacesAllDetailsTogether() {
        Product product = new Product("기존상품", 1000, "https://example.com/a");

        product.changeTo("새상품", 2000, "https://example.com/b", name -> !name.equals("새상품"));

        assertThat(product.name()).isEqualTo("새상품");
        assertThat(product.price()).isEqualTo(2000);
        assertThat(product.imageUrl()).isEqualTo("https://example.com/b");
    }

    @DisplayName("수정할 값이 유효하지 않으면 기존 상품 정보를 모두 유지한다")
    @ParameterizedTest
    @MethodSource("invalidChanges")
    void invalidChangesLeaveOriginalValues(String name, Integer price, String imageUrl) {
        Product product = new Product("기존상품", 1000, "https://example.com/a");

        assertThatIllegalArgumentException()
                .isThrownBy(() -> product.changeTo(name, price, imageUrl, text -> false));

        assertUnchanged(product);
    }

    @DisplayName("수정할 이름에 비속어가 있으면 기존 상품 정보를 모두 유지한다")
    @Test
    void profanityLeavesOriginalValues() {
        Product product = new Product("기존상품", 1000, "https://example.com/a");

        assertThatThrownBy(() -> product.changeTo("새상품", 2000, "https://b.com", text -> true))
                .isInstanceOfSatisfying(ShoppingException.class,
                        error -> assertThat(error.code()).isEqualTo(ErrorCode.PROFANITY_DETECTED));

        assertUnchanged(product);
    }

    @DisplayName("수정 시 비속어 검사에 실패하면 기존 상품 정보를 모두 유지한다")
    @Test
    void checkerFailureLeavesOriginalValues() {
        Product product = new Product("기존상품", 1000, "https://example.com/a");
        ShoppingException failure = new ShoppingException(ErrorCode.PROFANITY_CHECK_UNAVAILABLE);
        ProfanityChecker checker = text -> {
            throw failure;
        };

        assertThatThrownBy(() -> product.changeTo("새상품", 2000, "https://b.com", checker))
                .isSameAs(failure);

        assertUnchanged(product);
    }

    private static Stream<Arguments> invalidChanges() {
        return Stream.of(
                Arguments.of(null, 2000, "https://b.com"),
                Arguments.of("새상품", 0, "https://b.com"),
                Arguments.of("새상품", 2000, "ftp://b.com"));
    }

    private void assertUnchanged(Product product) {
        assertThat(product.name()).isEqualTo("기존상품");
        assertThat(product.price()).isEqualTo(1000);
        assertThat(product.imageUrl()).isEqualTo("https://example.com/a");
    }

    private Product product(String name, Integer price) {
        return new Product(name, price, "https://example.com/image.png");
    }
}
