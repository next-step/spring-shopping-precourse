package shopping;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import shopping.controller.LoginMemberArgumentResolver;
import shopping.domain.Email;
import shopping.domain.Member;
import shopping.domain.MemberId;
import shopping.domain.Members;
import shopping.domain.Password;
import shopping.domain.Product;
import shopping.domain.ProductId;
import shopping.domain.ProductImage;
import shopping.domain.ProductName;
import shopping.domain.ProductPrice;
import shopping.domain.Products;
import shopping.domain.WishList;
import shopping.service.AuthService;
import shopping.service.MemberService;
import shopping.service.ProductService;
import shopping.service.TokenStore;
import shopping.service.WishService;

/** 값이 없을 때 나오는 메시지도 개발 용어 없이 읽을 수 있는지 확인한다. */
class NullMessageTest {
    private static final List<String> JARGON = List.of("null", "ID", "id=", "URL", "JSON",
            "Exception");

    private static final Email EMAIL = new Email("user@gmail.com");
    private static final Password PASSWORD = new Password("abcd123!");
    private static final ProductName NAME = new ProductName("Item", text -> false);
    private static final ProductPrice PRICE = new ProductPrice(1);
    private static final ProductImage IMAGE = new ProductImage("https://a.com");
    private static final Members MEMBERS = new Members();
    private static final Products PRODUCTS = new Products();
    private static final TokenStore TOKENS = new TokenStore();
    private static final ProductService PRODUCT_SERVICE = new ProductService(PRODUCTS,
            text -> false);
    private static final WishService WISH_SERVICE = new WishService(PRODUCT_SERVICE);
    private static final Member MEMBER = new Member(EMAIL, PASSWORD);

    private static List<ThrowingCallable> nullCalls() {
        return List.of(
                () -> new Member(null, PASSWORD),
                () -> new Member(EMAIL, null),
                () -> new Member(null, EMAIL, PASSWORD),
                () -> new Member(new MemberId(1), null, PASSWORD),
                () -> new Member(new MemberId(1), EMAIL, null),
                () -> new Product(null, PRICE, IMAGE),
                () -> new Product(NAME, null, IMAGE),
                () -> new Product(NAME, PRICE, null),
                () -> new Product(null, NAME, PRICE, IMAGE),
                () -> PRODUCTS.add(null),
                () -> PRODUCTS.findById(null),
                () -> MEMBERS.add(null),
                () -> MEMBERS.findById(null),
                () -> MEMBERS.findByEmail(null),
                () -> new WishList().add(null),
                () -> new ProductName("Item", null),
                () -> TOKENS.issue(null),
                () -> new ProductService(null, text -> false),
                () -> new ProductService(PRODUCTS, null),
                () -> new MemberService(null, TOKENS),
                () -> new MemberService(MEMBERS, null),
                () -> new AuthService(null, TOKENS),
                () -> new AuthService(MEMBERS, null),
                () -> new WishService(null),
                () -> WISH_SERVICE.add(null, new ProductId(1)),
                () -> WISH_SERVICE.list(null),
                () -> WISH_SERVICE.remove(null, new ProductId(1)),
                () -> new LoginMemberArgumentResolver(null),
                () -> new Product(NAME, PRICE, IMAGE).hasName(null),
                () -> new Product(NAME, PRICE, IMAGE).hasId(null),
                () -> MEMBER.hasEmail(null),
                () -> MEMBER.matchesPassword(null),
                () -> PASSWORD.matches(null));
    }

    @Test
    @DisplayName("값이 없을 때의 메시지에도 null, ID 같은 개발 용어가 없다")
    void nullMessagesHaveNoJargon() {
        List<ThrowingCallable> calls = nullCalls();

        assertThat(calls).hasSize(33);
        for (ThrowingCallable call : calls) {
            assertThatThrownBy(call)
                    .isInstanceOf(NullPointerException.class)
                    .satisfies(e -> assertThat(e.getMessage()).isNotBlank())
                    .satisfies(e -> assertThat(JARGON).noneMatch(e.getMessage()::contains));
        }
        assertThat(MEMBER.email()).isEqualTo(EMAIL);
    }
}
