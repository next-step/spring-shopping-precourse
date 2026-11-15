package shopping.product;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatNoException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class ProductTest {
    private static final String IMAGE_URL = "https://example.com/a.png";

    @Test
    void 상품을_생성한다() {
        Product product = new Product("아이스 아메리카노", 4500, IMAGE_URL);

        assertThat(product.getName()).isEqualTo("아이스 아메리카노");
        assertThat(product.getPrice()).isEqualTo(4500);
        assertThat(product.getImageUrl()).isEqualTo(IMAGE_URL);
    }

    @ParameterizedTest
    @ValueSource(strings = {"가나다라마바사아자차카타파하 ", "(대)[소]+-&/_상품", "Coffee 2"})
    void 허용된_이름은_15자까지_사용할_수_있다(String name) {
        assertThatNoException().isThrownBy(() -> new Product(name, 1000, IMAGE_URL));
    }

    @Test
    void 이름이_15자를_넘으면_예외가_발생한다() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> new Product("가나다라마바사아자차카타파하 갸", 1000, IMAGE_URL));
    }

    @ParameterizedTest
    @ValueSource(strings = {"상품!", "상품@", "상품#", "상품*", "상품."})
    void 허용되지_않은_특수_문자를_쓰면_예외가_발생한다(String name) {
        assertThatIllegalArgumentException().isThrownBy(() -> new Product(name, 1000, IMAGE_URL));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   "})
    void 이름이_비어_있으면_예외가_발생한다(String name) {
        assertThatIllegalArgumentException().isThrownBy(() -> new Product(name, 1000, IMAGE_URL));
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1})
    void 가격이_1원_미만이면_예외가_발생한다(int price) {
        assertThatIllegalArgumentException().isThrownBy(() -> new Product("상품", price, IMAGE_URL));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"example.com/a.png", "ftp://example.com/a.png"})
    void 이미지_URL이_올바르지_않으면_예외가_발생한다(String imageUrl) {
        assertThatIllegalArgumentException().isThrownBy(() -> new Product("상품", 1000, imageUrl));
    }

    @Test
    void 상품을_수정한다() {
        Product product = new Product("상품", 1000, IMAGE_URL);

        product.update("새 상품", 2000, "http://example.com/b.png");

        assertThat(product.getName()).isEqualTo("새 상품");
        assertThat(product.getPrice()).isEqualTo(2000);
    }
}
