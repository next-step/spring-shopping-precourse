package shopping.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class WishTests {

    private static final Long MEMBER_ID = 1L;
    private static final Long OTHER_MEMBER_ID = 2L;

    private Member member;
    private Product product;

    @BeforeEach
    void setUp() {
        member = memberWithId("user@example.com", MEMBER_ID);
        product = Product.of("치킨", 20000, "https://api.dicebear.com/10.x/waves/svg");
    }

    @DisplayName("위시는 회원과 상품을 가진다")
    @Test
    void wishHasMemberAndProduct() {
        Wish wish = Wish.of(member, product);

        assertThat(wish.getMember()).isSameAs(member);
        assertThat(wish.getProduct()).isSameAs(product);
    }

    @DisplayName("회원이 null이면 예외가 발생한다")
    @Test
    void memberIsNull() {
        assertThatThrownBy(() -> Wish.of(null, product))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("상품이 null이면 예외가 발생한다")
    @Test
    void productIsNull() {
        assertThatThrownBy(() -> Wish.of(member, null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("위시를 담은 회원이면 소유자이다")
    @Test
    void isOwnedByOwner() {
        Wish wish = Wish.of(member, product);

        assertThat(wish.isOwnedBy(member)).isTrue();
    }

    @DisplayName("id가 같은 회원이면 다른 인스턴스여도 소유자이다")
    @Test
    void isOwnedBySameMemberWithDifferentInstance() {
        Wish wish = Wish.of(member, product);
        Member sameMember = memberWithId("user@example.com", MEMBER_ID);

        assertThat(wish.isOwnedBy(sameMember)).isTrue();
    }

    @DisplayName("다른 회원이면 소유자가 아니다")
    @Test
    void isNotOwnedByOtherMember() {
        Wish wish = Wish.of(member, product);
        Member otherMember = memberWithId("other@example.com", OTHER_MEMBER_ID);

        assertThat(wish.isOwnedBy(otherMember)).isFalse();
    }

    @DisplayName("회원이 null이면 소유자가 아니다")
    @Test
    void isNotOwnedByNull() {
        Wish wish = Wish.of(member, product);

        assertThat(wish.isOwnedBy(null)).isFalse();
    }

    private Member memberWithId(String email, Long id) {
        Member member = Member.of(email, "password123", new FakePasswordEncoder());
        ReflectionTestUtils.setField(member, "id", id);
        return member;
    }

    private static class FakePasswordEncoder implements PasswordEncoder {

        @Override
        public String encode(CharSequence rawPassword) {
            return "encoded:" + rawPassword;
        }

        @Override
        public boolean matches(CharSequence rawPassword, String encodedPassword) {
            return encode(rawPassword).equals(encodedPassword);
        }
    }
}
