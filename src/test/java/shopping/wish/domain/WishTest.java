package shopping.wish.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import shopping.common.error.ErrorCode;
import shopping.common.error.ShoppingException;
import shopping.product.domain.Product;

@DisplayName("위시 항목")
class WishTest {

    @DisplayName("위시 항목의 소유자만 삭제 권한 확인을 통과한다")
    @Test
    void onlyOwnerCanDeleteWish() {
        Product product = new Product("상품", 1000, "https://a.com");
        Wish wish = new Wish(7L, product);

        assertThatCode(() -> wish.requireOwner(7L)).doesNotThrowAnyException();
        assertThatThrownBy(() -> wish.requireOwner(8L))
                .isInstanceOfSatisfying(ShoppingException.class,
                        error -> assertThat(error.code()).isEqualTo(ErrorCode.FORBIDDEN));
    }

    @DisplayName("위시 생성에는 회원과 상품이 모두 필요하다")
    @Test
    void requiresMemberAndProductToCreateWish() {
        Product product = new Product("상품", 1000, "https://a.com");

        assertThatCode(() -> new Wish(7L, product)).doesNotThrowAnyException();
        assertThatIllegalArgumentException().isThrownBy(() -> new Wish(null, product));
        assertThatIllegalArgumentException().isThrownBy(() -> new Wish(7L, null));
    }
}
