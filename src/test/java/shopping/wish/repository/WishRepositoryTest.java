package shopping.wish.repository;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import shopping.member.domain.Member;
import shopping.member.repository.MemberRepository;
import shopping.product.domain.Product;
import shopping.product.repository.ProductRepository;
import shopping.wish.domain.Wish;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
class WishRepositoryTest {
    @Autowired
    private WishRepository wishRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private EntityManager entityManager;

    private Member member;
    private Member other;
    private Product product;

    @BeforeEach
    void setUp() {
        member = memberRepository.save(new Member(null, "a@a.com", "hashed"));
        other = memberRepository.save(new Member(null, "b@b.com", "hashed"));
        product = productRepository.save(
                new Product(null, "컵", 3000, "https://image.com/cup.png"));
    }

    @Test
    void 회원의_위시만_조회한다() {
        wishRepository.save(new Wish(null, member.getId(), product));
        wishRepository.save(new Wish(null, other.getId(), product));

        List<Wish> wishes = wishRepository.findAllByMemberId(member.getId());

        assertThat(wishes).hasSize(1);
        assertThat(wishes.get(0).getMemberId()).isEqualTo(member.getId());
    }

    @Test
    void 담은_위시가_없으면_빈_목록을_돌려준다() {
        assertThat(wishRepository.findAllByMemberId(member.getId())).isEmpty();
    }

    @Test
    void 담은_상품이면_존재한다고_답한다() {
        wishRepository.save(new Wish(null, member.getId(), product));

        assertThat(wishRepository.existsByMemberIdAndProductId(
                member.getId(), product.getId())).isTrue();
    }

    @Test
    void 다른_회원이_담은_상품은_존재하지_않는다고_답한다() {
        wishRepository.save(new Wish(null, other.getId(), product));

        assertThat(wishRepository.existsByMemberIdAndProductId(
                member.getId(), product.getId())).isFalse();
    }

    @Test
    void 같은_회원이_같은_상품을_두_번_저장하면_DB가_막는다() {
        wishRepository.saveAndFlush(new Wish(null, member.getId(), product));

        assertThatThrownBy(() -> wishRepository.saveAndFlush(
                new Wish(null, member.getId(), product)))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void 상품을_삭제하면_그_상품을_담은_위시도_함께_사라진다() {
        wishRepository.save(new Wish(null, member.getId(), product));
        entityManager.flush();
        entityManager.clear();

        productRepository.deleteById(product.getId());
        productRepository.flush();

        assertThat(wishRepository.findAllByMemberId(member.getId())).isEmpty();
    }
}
