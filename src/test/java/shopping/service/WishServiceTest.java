package shopping.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.NoSuchElementException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import shopping.domain.Email;
import shopping.domain.Member;
import shopping.domain.Password;
import shopping.domain.Product;
import shopping.domain.ProductId;
import shopping.domain.Products;

class WishServiceTest {
    private static final String URL = "https://example.com/a.png";

    private ProductService productService;
    private WishService service;
    private Member member;
    private Member other;

    private static Member member(String email) {
        return new Member(new Email(email), new Password("abcd123!"));
    }

    @BeforeEach
    void setUp() {
        productService = new ProductService(new Products(), text -> false);
        service = new WishService(productService);
        member = member("user@gmail.com");
        other = member("other@gmail.com");
    }

    @Test
    @DisplayName("상품을 위시 리스트에 추가하면 조회된다")
    void add() {
        Product product = productService.create("First", 1000, URL);

        Product added = service.add(member, new ProductId(1));

        assertThat(added).isSameAs(product);
        assertThat(service.list(member)).containsExactly(product);
    }

    @Test
    @DisplayName("존재하지 않는 상품은 추가할 수 없다")
    void addUnknownProduct() {
        assertThatThrownBy(() -> service.add(member, new ProductId(9)))
                .isInstanceOf(NoSuchElementException.class);

        assertThat(service.list(member)).isEmpty();
    }

    @Test
    @DisplayName("같은 상품을 두 번 추가할 수 없고 기존 목록은 유지된다")
    void addDuplicate() {
        productService.create("First", 1000, URL);
        service.add(member, new ProductId(1));

        assertThatThrownBy(() -> service.add(member, new ProductId(1)))
                .isInstanceOf(IllegalArgumentException.class);

        assertThat(service.list(member)).hasSize(1);
    }

    @Test
    @DisplayName("위시 리스트는 담은 순서대로 조회되고 처음에는 비어 있다")
    void listOrder() {
        productService.create("First", 1, URL);
        productService.create("Second", 2, URL);
        assertThat(service.list(member)).isEmpty();

        service.add(member, new ProductId(2));
        service.add(member, new ProductId(1));

        assertThat(service.list(member)).extracting(p -> p.name().value())
                .containsExactly("Second", "First");
    }

    @Test
    @DisplayName("위시 리스트는 회원마다 따로이고 같은 상품을 각자 담을 수 있다")
    void listIsPerMember() {
        productService.create("First", 1, URL);

        service.add(member, new ProductId(1));
        service.add(other, new ProductId(1));

        assertThat(service.list(member)).hasSize(1);
        assertThat(service.list(other)).hasSize(1);
    }

    @Test
    @DisplayName("담은 상품을 삭제할 수 있다")
    void remove() {
        productService.create("First", 1, URL);
        service.add(member, new ProductId(1));

        service.remove(member, new ProductId(1));

        assertThat(service.list(member)).isEmpty();
    }

    @Test
    @DisplayName("담겨 있지 않은 상품을 삭제하면 예외가 발생한다")
    void removeNotInWishList() {
        productService.create("First", 1, URL);

        assertThatThrownBy(() -> service.remove(member, new ProductId(1)))
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    @DisplayName("다른 회원이 담은 상품은 삭제할 수 없고 그 회원의 목록은 유지된다")
    void removeOthersWish() {
        productService.create("First", 1, URL);
        service.add(other, new ProductId(1));

        assertThatThrownBy(() -> service.remove(member, new ProductId(1)))
                .isInstanceOf(NoSuchElementException.class);

        assertThat(service.list(other)).hasSize(1);
    }

    @Test
    @DisplayName("상품 정보를 수정하면 위시 리스트에도 반영된다")
    void productUpdateIsReflected() {
        productService.create("First", 1, URL);
        service.add(member, new ProductId(1));

        productService.update(new ProductId(1), "Renamed", 5, URL);

        assertThat(service.list(member).get(0).name().value()).isEqualTo("Renamed");
        assertThat(service.list(member).get(0).price().value()).isEqualTo(5);
    }

    @Test
    @DisplayName("상품이 삭제되면 위시 리스트 조회에서 보이지 않고, 담아둔 항목은 삭제할 수 있다")
    void deletedProductIsHidden() {
        productService.create("First", 1, URL);
        productService.create("Second", 2, URL);
        service.add(member, new ProductId(1));
        service.add(member, new ProductId(2));

        productService.delete(new ProductId(1));

        assertThat(service.list(member)).extracting(p -> p.name().value())
                .containsExactly("Second");
        service.remove(member, new ProductId(1));
        assertThatThrownBy(() -> service.add(member, new ProductId(1)))
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    @DisplayName("회원이 null이면 예외가 발생한다")
    void nullMember() {
        assertThatThrownBy(() -> service.list(null))
                .isInstanceOf(NullPointerException.class);
    }
}
