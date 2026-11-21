package shopping.repository;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import shopping.model.Email;
import shopping.model.Member;
import shopping.model.PasswordHash;
import shopping.model.Product;
import shopping.model.ProductName;
import shopping.model.Wish;

class WishRepositoryTest {
    private WishRepository wishRepository;
    private Member firstMember;
    private Member secondMember;
    private Product product;

    @BeforeEach
    void setUp() {
        wishRepository = new WishRepository();
        MemberRepository memberRepository = new MemberRepository();
        ProductRepository productRepository = new ProductRepository();
        firstMember = memberRepository.save(createMember("first@example.com"));
        secondMember = memberRepository.save(createMember("second@example.com"));
        product = productRepository.save(
                new Product(new ProductName("상품"), 1_000L, "image-url"));
    }

    private Member createMember(String email) {
        return new Member(new Email(email), new PasswordHash("password"));
    }

    @Test
    @DisplayName("회원별 위시리스트만 조회된다")
    void findWishesByMember() {
        Wish firstWish = wishRepository.save(new Wish(firstMember, product));
        wishRepository.save(new Wish(secondMember, product));

        assertThat(wishRepository.findAllByMemberId(firstMember.getId()))
                .containsExactly(firstWish);
    }

    @Test
    @DisplayName("다른 회원의 위시리스트 항목은 조회되지 않는다")
    void doNotFindOtherMembersWish() {
        Wish wish = wishRepository.save(new Wish(firstMember, product));

        assertThat(wishRepository.findByIdAndMemberId(wish.getId(), secondMember.getId()))
                .isEmpty();
    }

    @Test
    @DisplayName("위시리스트 항목을 삭제하면 목록에서 제거된다")
    void deleteWish() {
        Wish wish = wishRepository.save(new Wish(firstMember, product));

        wishRepository.delete(wish);

        assertThat(wishRepository.findAllByMemberId(firstMember.getId())).isEmpty();
    }
}
