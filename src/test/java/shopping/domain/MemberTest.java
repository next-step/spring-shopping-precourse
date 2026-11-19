package shopping.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class MemberTest {
    private static final ProfanityChecker NO_PROFANITY = text -> false;

    private static Member member(String email) {
        return new Member(new Email(email), new Password("abcd123!"));
    }

    private static Product product(long id, String name) {
        return new Product(new ProductId(id), new ProductName(name, NO_PROFANITY),
                new ProductPrice(1000), new ProductImage("https://example.com/a.png"));
    }

    @Test
    @DisplayName("이메일과 비밀번호로 회원을 생성하면 위시 리스트는 비어 있다")
    void create() {
        Member member = member("user@gmail.com");

        assertThat(member.email()).isEqualTo(new Email("user@gmail.com"));
        assertThat(member.password()).isEqualTo(new Password("abcd123!"));
        assertThat(member.wishList().all()).isEmpty();
    }

    @Test
    @DisplayName("이메일이나 비밀번호가 null이면 회원을 만들 수 없다")
    void createWithNull() {
        Email email = new Email("user@gmail.com");
        Password password = new Password("abcd123!");

        assertThatThrownBy(() -> new Member(null, password))
                .isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> new Member(email, null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("회원의 위시 리스트에 상품을 추가하고 삭제할 수 있다")
    void wishListAddAndRemove() {
        Member member = member("user@gmail.com");

        member.wishList().add(product(1, "First"));
        member.wishList().add(product(2, "Second"));
        member.wishList().remove(new ProductId(1));

        assertThat(member.wishList().all()).hasSize(1);
        assertThat(member.wishList().all().get(0).name().value()).isEqualTo("Second");
    }

    @Test
    @DisplayName("회원의 위시 리스트도 같은 ID의 상품을 중복해서 담을 수 없다")
    void wishListDuplicate() {
        Member member = member("user@gmail.com");
        member.wishList().add(product(1, "First"));

        assertThatThrownBy(() -> member.wishList().add(product(1, "Other")))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("회원마다 위시 리스트가 따로다")
    void wishListIsPerMember() {
        Member first = member("first@gmail.com");
        Member second = member("second@gmail.com");

        first.wishList().add(product(1, "First"));

        assertThat(first.wishList().all()).hasSize(1);
        assertThat(second.wishList().all()).isEmpty();
    }
}
