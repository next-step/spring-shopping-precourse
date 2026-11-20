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
        assertThat(member.wishes()).isEmpty();
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

        member.addWish(product(1, "First"));
        member.addWish(product(2, "Second"));
        member.removeWish(new ProductId(1));

        assertThat(member.wishes()).hasSize(1);
        assertThat(member.wishes().get(0).name().value()).isEqualTo("Second");
    }

    @Test
    @DisplayName("회원의 위시 리스트도 같은 ID의 상품을 중복해서 담을 수 없다")
    void wishListDuplicate() {
        Member member = member("user@gmail.com");
        member.addWish(product(1, "First"));

        assertThatThrownBy(() -> member.addWish(product(1, "Other")))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("회원마다 위시 리스트가 따로다")
    void wishListIsPerMember() {
        Member first = member("first@gmail.com");
        Member second = member("second@gmail.com");

        first.addWish(product(1, "First"));

        assertThat(first.wishes()).hasSize(1);
        assertThat(second.wishes()).isEmpty();
    }

    @Test
    @DisplayName("ID 없이 만든 회원(아직 가입 전)은 ID가 없다")
    void createWithoutId() {
        assertThat(member("user@gmail.com").id()).isEmpty();
    }

    @Test
    @DisplayName("ID와 함께 회원을 생성할 수 있다")
    void createWithId() {
        Member member = new Member(new MemberId(3), new Email("user@gmail.com"),
                new Password("abcd123!"));

        assertThat(member.id()).contains(new MemberId(3));
        assertThat(member.email()).isEqualTo(new Email("user@gmail.com"));
        assertThat(member.wishes()).isEmpty();
    }

    @Test
    @DisplayName("ID를 null로 넘기면 회원을 만들 수 없다")
    void createWithNullId() {
        Email email = new Email("user@gmail.com");
        Password password = new Password("abcd123!");

        assertThatThrownBy(() -> new Member(null, email, password))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("회원이 자기 이메일과 같은지 스스로 알려준다")
    void hasEmail() {
        Member member = member("user@gmail.com");

        assertThat(member.hasEmail(new Email("user@gmail.com"))).isTrue();
        assertThat(member.hasEmail(new Email("other@gmail.com"))).isFalse();
    }

    @Test
    @DisplayName("회원이 입력한 비밀번호가 자기 비밀번호와 맞는지 스스로 알려준다")
    void matchesPassword() {
        Member member = member("user@gmail.com");

        assertThat(member.matchesPassword(new Password("abcd123!"))).isTrue();
        assertThat(member.matchesPassword(new Password("zzzz9999!"))).isFalse();
    }

    @Test
    @DisplayName("이메일이나 비밀번호를 null로 물으면 예외가 발생한다")
    void askWithNull() {
        Member member = member("user@gmail.com");

        assertThatThrownBy(() -> member.hasEmail(null))
                .isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> member.matchesPassword(null))
                .isInstanceOf(NullPointerException.class);
    }
}
