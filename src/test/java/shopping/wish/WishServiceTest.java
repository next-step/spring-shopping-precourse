package shopping.wish;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import shopping.exception.ConflictException;
import shopping.exception.ForbiddenException;
import shopping.exception.NotFoundException;
import shopping.member.Member;
import shopping.member.MemberRepository;
import shopping.product.ProductService;
import shopping.wish.dto.WishResponse;

@SpringBootTest
@Transactional
class WishServiceTest {

    @Autowired
    private WishService wishService;

    @Autowired
    private ProductService productService;

    @Autowired
    private MemberRepository memberRepository;

    private Long memberId;
    private Long otherMemberId;
    private Long productId;

    @BeforeEach
    void setUp() {
        memberId = memberRepository.save(new Member("a@test.com", "1234")).getId();
        otherMemberId = memberRepository.save(new Member("b@test.com", "1234")).getId();
        productId = productService.create("키보드", 50000, "http://img.com/a.png").getId();
    }

    @Test
    void 위시_리스트에_상품을_추가하고_조회한다() {
        wishService.add(memberId, productId);

        assertThat(wishService.findAll(memberId)).hasSize(1);
        assertThat(wishService.findAll(otherMemberId)).isEmpty();
    }

    @Test
    void 이미_담은_상품을_다시_추가하면_예외가_발생한다() {
        wishService.add(memberId, productId);

        assertThatThrownBy(() -> wishService.add(memberId, productId))
                .isInstanceOf(ConflictException.class);
    }

    @Test
    void 존재하지_않는_상품을_추가하면_예외가_발생한다() {
        assertThatThrownBy(() -> wishService.add(memberId, 999L))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void 위시를_삭제한다() {
        WishResponse wish = wishService.add(memberId, productId);

        wishService.delete(memberId, wish.id());

        assertThat(wishService.findAll(memberId)).isEmpty();
    }

    @Test
    void 다른_회원의_위시를_삭제하면_예외가_발생한다() {
        WishResponse wish = wishService.add(memberId, productId);

        assertThatThrownBy(() -> wishService.delete(otherMemberId, wish.id()))
                .isInstanceOf(ForbiddenException.class);
    }

    @Test
    void 존재하지_않는_위시를_삭제하면_예외가_발생한다() {
        assertThatThrownBy(() -> wishService.delete(memberId, 999L))
                .isInstanceOf(NotFoundException.class);
    }
}