package shopping.wish;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import shopping.common.DuplicateException;
import shopping.common.ForbiddenException;
import shopping.common.NotFoundException;
import shopping.member.Member;
import shopping.member.MemberRepository;
import shopping.product.Product;
import shopping.product.ProductRepository;
import shopping.wish.dto.WishRequest;
import shopping.wish.dto.WishResponse;

@SpringBootTest
@Transactional
class WishServiceTest {

    @Autowired
    private WishService wishService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ProductRepository productRepository;

    private Member memberA;
    private Member memberB;
    private Product product;

    @BeforeEach
    void setUp() {
        memberA = memberRepository.save(new Member("a@test.com", "1234"));
        memberB = memberRepository.save(new Member("b@test.com", "1234"));
        product = productRepository.save(
                new Product("딸기", 1000, "https://example.com/strawberry.jpg"));
    }

    @Test
    @DisplayName("위시에 상품을 담을 수 있다")
    void addsWish() {
        WishResponse response = wishService.add(memberA, new WishRequest(product.getId()));
        assertThat(response.id()).isNotNull();
        assertThat(response.product().name()).isEqualTo("딸기");
    }

    @Test
    @DisplayName("같은 상품을 두 번 담으면 예외가 발생한다")
    void throwsWhenDuplicate() {
        wishService.add(memberA, new WishRequest(product.getId()));
        assertThatThrownBy(() -> wishService.add(memberA, new WishRequest(product.getId())))
                .isInstanceOf(DuplicateException.class);
    }

    @Test
    @DisplayName("없는 상품을 담으면 예외가 발생한다")
    void throwsWhenNoProduct() {
        assertThatThrownBy(() -> wishService.add(memberA, new WishRequest(999L)))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    @DisplayName("내 위시 목록에는 내가 담은 것만 나온다")
    void findsOnlyMyWishes() {
        wishService.add(memberA, new WishRequest(product.getId()));
        assertThat(wishService.findAll(memberA)).hasSize(1);
        assertThat(wishService.findAll(memberB)).isEmpty();
    }

    @Test
    @DisplayName("내 위시는 삭제할 수 있다")
    void deletesMyWish() {
        WishResponse saved = wishService.add(memberA, new WishRequest(product.getId()));
        wishService.delete(memberA, saved.id());
        assertThat(wishService.findAll(memberA)).isEmpty();
    }

    @Test
    @DisplayName("남의 위시를 삭제하면 예외가 발생한다")
    void throwsWhenNotOwner() {
        WishResponse saved = wishService.add(memberA, new WishRequest(product.getId()));
        assertThatThrownBy(() -> wishService.delete(memberB, saved.id()))
                .isInstanceOf(ForbiddenException.class);
    }

    @Test
    @DisplayName("없는 위시를 삭제하면 예외가 발생한다")
    void throwsWhenNoWish() {
        assertThatThrownBy(() -> wishService.delete(memberA, 999L))
                .isInstanceOf(NotFoundException.class);
    }
}