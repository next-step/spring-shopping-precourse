package shopping.wish.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;
import shopping.member.domain.Member;
import shopping.member.repository.MemberRepository;
import shopping.product.domain.Product;
import shopping.product.exception.ProductNotFoundException;
import shopping.product.repository.ProductRepository;
import shopping.wish.domain.Wish;
import shopping.wish.exception.DuplicateWishException;
import shopping.wish.exception.WishAccessDeniedException;
import shopping.wish.exception.WishNotFoundException;
import shopping.wish.repository.WishRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@Import(WishService.class)
class WishServiceTest {
    @Autowired
    private WishService wishService;

    @Autowired
    private WishRepository wishRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ProductRepository productRepository;

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
    void 상품을_담으면_위시가_저장된다() {
        Wish wish = wishService.add(member.getId(), product.getId());

        assertThat(wish.getId()).isNotNull();
        assertThat(wish.getProduct().getName()).isEqualTo("컵");
        assertThat(wishRepository.findAllByMemberId(member.getId())).hasSize(1);
    }

    @Test
    void 없는_상품을_담으면_예외가_나고_저장되지_않는다() {
        assertThatThrownBy(() -> wishService.add(member.getId(), 999L))
                .isInstanceOf(ProductNotFoundException.class);
        assertThat(wishRepository.count()).isZero();
    }

    @Test
    void 같은_상품을_또_담으면_예외가_나고_하나만_남는다() {
        wishService.add(member.getId(), product.getId());

        assertThatThrownBy(() -> wishService.add(member.getId(), product.getId()))
                .isInstanceOf(DuplicateWishException.class);
        assertThat(wishRepository.count()).isEqualTo(1);
    }

    @Test
    void 다른_회원인_같은_상품을_담을_수_있다() {
        wishService.add(member.getId(), product.getId());
        wishService.add(other.getId(), product.getId());

        List<Wish> wishes = wishService.findAll(member.getId());

        assertThat(wishes).hasSize(1);
        assertThat(wishes.get(0).getMemberId()).isEqualTo(member.getId());
    }

    @Test
    void 조회하면_내_위시만_나온다() {
        wishService.add(member.getId(), product.getId());
        wishService.add(other.getId(), product.getId());

        List<Wish> wishes = wishService.findAll(member.getId());

        assertThat(wishes).hasSize(1);
        assertThat(wishes.get(0).getMemberId()).isEqualTo(member.getId());
    }

    @Test
    void 삭제하면_그_위시는_더_이상_조회되지_않는다() {
        Wish wish = wishService.add(member.getId(), product.getId());

        wishService.delete(member.getId(), wish.getId());

        assertThat(wishRepository.findById(wish.getId())).isEmpty();
    }

    @Test
    void 없는_위시를_삭제하면_예외가_난다() {
        assertThatThrownBy(() -> wishService.delete(member.getId(), 999L))
                .isInstanceOf(WishNotFoundException.class);
    }

    @Test
    void 남의_위시를_삭제하면_예외가_나고_위시는_그대로다() {
        Wish wish = wishService.add(member.getId(), product.getId());

        assertThatThrownBy(() -> wishService.delete(other.getId(), wish.getId()))
                .isInstanceOf(WishAccessDeniedException.class);
        assertThat(wishRepository.findById(wish.getId())).isPresent();
    }
}
