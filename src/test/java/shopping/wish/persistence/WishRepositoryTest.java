package shopping.wish.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import jakarta.persistence.EntityManager;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import shopping.member.domain.Email;
import shopping.member.domain.Member;
import shopping.member.domain.Password;
import shopping.member.persistence.MemberRepository;
import shopping.product.domain.Product;
import shopping.product.persistence.ProductRepository;
import shopping.wish.domain.Wish;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@DisplayName("위시 저장소")
class WishRepositoryTest {

    @Autowired
    private WishRepository wishes;
    @Autowired
    private MemberRepository members;
    @Autowired
    private ProductRepository products;
    @Autowired
    private EntityManager entityManager;
    private Long owner;
    private Long other;
    private Product product;

    @BeforeEach
    void prepare() {
        wishes.deleteAll();
        owner = member("repo-owner@example.com").id();
        other = member("repo-other@example.com").id();
        product = products.saveAndFlush(product("상품"));
    }

    @DisplayName("회원별 위시를 ID순으로 조회하고 영속성 컨텍스트 밖에서도 상품을 읽는다")
    @Test
    void filtersByMemberAndLoadsOrderedProductsOutsidePersistenceContext() {
        Wish first = wishes.saveAndFlush(new Wish(owner, product));
        wishes.saveAndFlush(new Wish(other, product));
        Product next = products.saveAndFlush(product("다음상품"));
        Wish second = wishes.saveAndFlush(new Wish(owner, next));
        entityManager.clear();

        List<Wish> found = wishes.findAllWithProducts(owner);
        entityManager.clear();

        assertThat(found).extracting(Wish::id).containsExactly(first.id(), second.id());
        assertThat(found).extracting(wish -> wish.product().name())
                .containsExactly("상품", "다음상품");
    }

    @DisplayName("서로 다른 회원은 같은 상품을 위시에 저장할 수 있다")
    @Test
    void sameProductCanBeStoredForDifferentMembers() {
        wishes.saveAndFlush(new Wish(owner, product));
        wishes.saveAndFlush(new Wish(other, product));

        assertThat(wishes.existsByMemberIdAndProduct_Id(owner, product.id())).isTrue();
        assertThat(wishes.existsByMemberIdAndProduct_Id(other, product.id())).isTrue();
    }

    @DisplayName("저장소는 같은 회원의 중복 상품 저장을 거절한다")
    @Test
    void databaseRejectsDuplicateMemberProductPair() {
        wishes.saveAndFlush(new Wish(owner, product));

        assertThatThrownBy(() -> wishes.saveAndFlush(new Wish(owner, product)))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @DisplayName("상품 삭제 시 모든 회원의 해당 상품 위시만 함께 삭제한다")
    @Test
    void productDeletionRemovesOnlyRelatedWishesAcrossMembers() {
        wishes.saveAndFlush(new Wish(owner, product));
        wishes.saveAndFlush(new Wish(other, product));
        Product unrelated = products.saveAndFlush(product("다른상품"));
        Wish retained = wishes.saveAndFlush(new Wish(owner, unrelated));
        entityManager.clear();

        products.delete(product);
        products.flush();
        entityManager.clear();

        assertThat(wishes.findAll()).extracting(Wish::id).containsExactly(retained.id());
        assertThat(products.findById(product.id())).isEmpty();
    }

    private Member member(String email) {
        return members.saveAndFlush(new Member(new Email(email), new Password("hello123")));
    }

    private Product product(String name) {
        return new Product(name, 1000, "https://example.com/a");
    }
}
