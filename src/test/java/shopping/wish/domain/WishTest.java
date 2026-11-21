package shopping.wish.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import shopping.member.domain.Member;
import shopping.product.domain.Product;

class WishTest {

    @Test
    @DisplayName("위시의 소유 회원을 확인한다")
    void isOwnedBy() {
        Member member = new Member("user@test.com", "password");
        ReflectionTestUtils.setField(member, "id", 1L);
        Wish wish = new Wish(member,
                new Product("상품", 1_000L, "https://example.com/product.jpg"));

        assertThat(wish.isOwnedBy(1L)).isTrue();
        assertThat(wish.isOwnedBy(2L)).isFalse();
    }
}
