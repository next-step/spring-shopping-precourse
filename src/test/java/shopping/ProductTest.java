package shopping;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class ProductTest {

    private static final String VALID_NAME = "아이폰 듀오";
    private static final long VALID_PRICE = 1000L;
    private static final String VALID_IMAGE_URL = "https://example.com/a.png";

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "       "})
    void 이름은_빈_문자열이거나_공백으로만_이루어질_수_없다(String name) {
        assertThatThrownBy(() -> new Product(name, VALID_PRICE, VALID_IMAGE_URL))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("상품 이름은 비어 있을 수 없습니다.");
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "1234567890123456",
            "12345678901234567",
            "123456789012345 6"
    })
    void 이름은_공백을_포함하여_15자를_초과할_수_없다(String name) {
        assertThatThrownBy(() -> new Product(name, VALID_PRICE, VALID_IMAGE_URL))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "상품!",
            "상품@",
            "상품#",
            "상품$",
            "상품%",
            "상품*",
            "상품?"
    })
    void 이름에는_허용되지_않은_특수문자를_사용할_수_없다(String name) {
        assertThatThrownBy(() -> new Product(name, VALID_PRICE, VALID_IMAGE_URL))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "상품(대)",
            "상품[특가]",
            "상품+추가",
            "상품-할인",
            "상품&쿠폰",
            "상품/배송",
            "상품_이름"
    })
    void 이름에는_허용된_특수문자를_사용할_수_있다(String name) {
        assertThatCode(() -> new Product(name, VALID_PRICE, VALID_IMAGE_URL))
                .doesNotThrowAnyException();
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, 0, 1_000_001})
    void 가격은_0보다_크고_100만원_이하여야_한다(int price) {
        assertThatThrownBy(() ->
                new Product(VALID_NAME, price, VALID_IMAGE_URL))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("상품 가격은 1원 이상 1,000,000원 이하이어야 합니다.");
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 999_999, 1_000_000})
    void 유효한_범위의_가격으로_상품을_생성할_수_있다(int price) {
        assertThatCode(() -> new Product(VALID_NAME, price, VALID_IMAGE_URL))
                .doesNotThrowAnyException();
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {
            " ",
            "example.com/a.png",
            "ftp://example.com/a.png",
            "httpsexample.com/a.png",
            "//example.com/a.png",
            " https://example.com/a.png",
            "HTTPS://example.com/a.png"
    })
    void 이미지_URL은_http_혹은_https로_시작해야_한다(String imageUrl) {
        assertThatThrownBy(() -> new Product(VALID_NAME, VALID_PRICE, imageUrl))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("이미지 URL은 http:// 혹은 https://로 시작해야 합니다.");
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "http://example.com/a.png",
            "https://example.com/a.png"
    })
    void http_혹은_https로_시작하는_이미지_URL로_상품을_생성할_수_있다(String imageUrl) {
        assertThatCode(() -> new Product(VALID_NAME, VALID_PRICE, imageUrl))
                .doesNotThrowAnyException();
    }
}
