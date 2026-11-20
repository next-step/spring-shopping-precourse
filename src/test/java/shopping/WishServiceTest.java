package shopping;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@DataJpaTest(
        properties = {
            "spring.flyway.enabled=false",
            "spring.datasource.password=",
            "spring.jpa.hibernate.ddl-auto=create-drop"
        })
@Import({WishService.class, ProductService.class})
class WishServiceTest {

    @Autowired private WishService service;

    @Autowired private ProductService productService;

    @Autowired private WishRepository wishes;

    @Autowired private MemberRepository members;

    @Autowired private ProductRepository products;

    @Autowired private EntityManager entityManager;

    @MockitoBean private ProfanityChecker profanityChecker;

    private Member owner;
    private Member other;
    private Product product;

    @BeforeEach
    void setUp() {
        owner = members.save(new Member("owner@example.com", "hash"));
        other = members.save(new Member("other@example.com", "hash"));
        product = products.saveAndFlush(new Product("keyboard", 1000, "https://example.com/a.jpg"));
    }

    @Test
    void addsWishWithProductDetails() {
        WishResponse response = service.add(owner.getId(), product.getId());
        entityManager.clear();
        Wish stored = wishes.findById(response.id()).orElseThrow();
        assertThat(stored.isOwnedBy(owner.getId())).isTrue();
        assertThat(response.product().id()).isEqualTo(product.getId());
        assertThat(response.product().name()).isEqualTo("keyboard");
    }

    @Test
    void rejectsDuplicateForSameMember() {
        service.add(owner.getId(), product.getId());
        assertThatThrownBy(() -> service.add(owner.getId(), product.getId()))
                .isInstanceOf(DuplicateWishException.class);
        assertThat(wishes.count()).isEqualTo(1);
    }

    @Test
    void differentMembersCanWishForSameProduct() {
        WishResponse first = service.add(owner.getId(), product.getId());
        WishResponse second = service.add(other.getId(), product.getId());
        assertThat(first.id()).isNotEqualTo(second.id());
        assertThat(wishes.count()).isEqualTo(2);
    }

    @Test
    void rejectsMissingProductWithoutSaving() {
        assertThatThrownBy(() -> service.add(owner.getId(), -1L))
                .isInstanceOf(ProductNotFoundException.class);
        assertThat(wishes.count()).isZero();
    }

    @Test
    void listsOnlyRequestedMembersWishes() {
        WishResponse owned = service.add(owner.getId(), product.getId());
        service.add(other.getId(), product.getId());
        entityManager.clear();
        assertThat(service.findAll(owner.getId()))
                .extracting(WishResponse::id)
                .containsExactly(owned.id());
    }

    @Test
    void returnsEmptyListWhenMemberHasNoWishes() {
        service.add(other.getId(), product.getId());
        assertThat(service.findAll(owner.getId())).isEmpty();
    }

    @Test
    void deletesOwnedWishWithoutDeletingProductOrOtherMembersWish() {
        WishResponse owned = service.add(owner.getId(), product.getId());
        WishResponse others = service.add(other.getId(), product.getId());
        service.delete(owner.getId(), owned.id());
        entityManager.flush();
        entityManager.clear();
        assertThat(wishes.findById(owned.id())).isEmpty();
        assertThat(wishes.findById(others.id())).isPresent();
        assertThat(products.findById(product.getId())).isPresent();
    }

    @Test
    void forbidsDeletingAnotherMembersWish() {
        WishResponse owned = service.add(owner.getId(), product.getId());
        assertThatThrownBy(() -> service.delete(other.getId(), owned.id()))
                .isInstanceOf(WishAccessDeniedException.class);
        assertThat(wishes.findById(owned.id())).isPresent();
    }

    @Test
    void rejectsDeletingMissingWish() {
        assertThatThrownBy(() -> service.delete(owner.getId(), -1L))
                .isInstanceOf(WishNotFoundException.class);
    }

    @Test
    void deletingProductRemovesItsWishesForAllMembersOnly() {
        service.add(owner.getId(), product.getId());
        service.add(other.getId(), product.getId());
        Product kept =
                products.saveAndFlush(new Product("mouse", 2000, "https://example.com/b.jpg"));
        WishResponse keptWish = service.add(owner.getId(), kept.getId());
        productService.delete(product.getId());
        entityManager.flush();
        entityManager.clear();
        assertThat(products.findById(product.getId())).isEmpty();
        assertThat(products.findById(kept.getId())).isPresent();
        assertThat(wishes.findAll()).extracting(Wish::getId).containsExactly(keptWish.id());
    }

    @Test
    void wishDisplaysLatestProductDetails() {
        service.add(owner.getId(), product.getId());
        productService.update(product.getId(), "updated", 2500, "https://example.com/b.jpg");
        entityManager.flush();
        entityManager.clear();
        ProductResponse response = service.findAll(owner.getId()).getFirst().product();
        assertThat(response.name()).isEqualTo("updated");
        assertThat(response.price()).isEqualTo(2500);
    }
}
