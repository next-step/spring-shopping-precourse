package shopping.repository;

import org.hibernate.Hibernate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import shopping.entity.Member;
import shopping.entity.PasswordEncoder;
import shopping.entity.Product;
import shopping.entity.Wish;
import shopping.security.BCryptPasswordEncoderAdapter;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@Import(BCryptPasswordEncoderAdapter.class)
class WishRepositoryTests {

    private static final String IMAGE_URL = "https://api.dicebear.com/10.x/waves/svg";

    @Autowired
    private WishRepository wishRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private TestEntityManager entityManager;

    @DisplayName("위시를 저장하면 id가 생성된다")
    @Test
    void save() {
        Member member = saveMember("user@example.com");
        Product product = saveProduct("치킨");

        Wish saved = saveWish(member, product);

        assertThat(saved.getId()).isNotNull();
    }

    @DisplayName("같은 회원이 같은 상품을 두 번 담으면 유니크 제약 조건 위반 예외가 발생한다")
    @Test
    void duplicateWishViolatesUniqueConstraint() {
        Member member = saveMember("user@example.com");
        Product product = saveProduct("치킨");
        wishRepository.saveAndFlush(Wish.of(member, product));

        assertThatThrownBy(() -> wishRepository.saveAndFlush(Wish.of(member, product)))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @DisplayName("같은 회원이 다른 상품을 담을 수 있다")
    @Test
    void sameMemberCanWishDifferentProducts() {
        Member member = saveMember("user@example.com");
        wishRepository.saveAndFlush(Wish.of(member, saveProduct("치킨")));
        wishRepository.saveAndFlush(Wish.of(member, saveProduct("피자")));

        assertThat(wishRepository.findAll()).hasSize(2);
    }

    @DisplayName("다른 회원이 같은 상품을 담을 수 있다")
    @Test
    void differentMembersCanWishSameProduct() {
        Product product = saveProduct("치킨");
        wishRepository.saveAndFlush(Wish.of(saveMember("first@example.com"), product));
        wishRepository.saveAndFlush(Wish.of(saveMember("second@example.com"), product));

        assertThat(wishRepository.findAll()).hasSize(2);
    }

    @DisplayName("상품을 삭제하면 그 상품을 담은 위시도 함께 삭제된다")
    @Test
    void deletingProductDeletesItsWishes() {
        Wish wish = saveWish(saveMember("user@example.com"), saveProduct("치킨"));
        Long productId = wish.getProduct().getId();
        flushAndClear();

        productRepository.deleteById(productId);
        flushAndClear();

        assertThat(wishRepository.findAll()).isEmpty();
    }

    @DisplayName("회원의 위시를 조회하면 상품도 함께 가져온다")
    @Test
    void findAllByMemberIdFetchesProduct() {
        Member member = saveMember("user@example.com");
        saveWish(member, saveProduct("치킨"));
        saveWish(member, saveProduct("피자"));
        flushAndClear();

        List<Wish> wishes = wishRepository.findAllByMemberId(member.getId());

        assertThat(wishes).hasSize(2);
        assertThat(wishes).allSatisfy(wish -> assertThat(Hibernate.isInitialized(wish.getProduct())).isTrue());
    }

    @DisplayName("다른 회원의 위시는 조회하지 않는다")
    @Test
    void findAllByMemberIdOnlyReturnsOwnWishes() {
        Member member = saveMember("user@example.com");
        Member otherMember = saveMember("other@example.com");
        Product product = saveProduct("치킨");
        Wish own = saveWish(member, product);
        saveWish(otherMember, product);
        flushAndClear();

        List<Wish> wishes = wishRepository.findAllByMemberId(member.getId());

        assertThat(wishes).extracting(Wish::getId).containsExactly(own.getId());
    }

    private Member saveMember(String email) {
        return memberRepository.save(Member.of(email, "password123", passwordEncoder));
    }

    private Product saveProduct(String name) {
        return productRepository.save(Product.of(name, 20000, IMAGE_URL));
    }

    private Wish saveWish(Member member, Product product) {
        return wishRepository.save(Wish.of(member, product));
    }

    private void flushAndClear() {
        entityManager.flush();
        entityManager.clear();
    }
}
