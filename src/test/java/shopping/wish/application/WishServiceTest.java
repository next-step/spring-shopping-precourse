package shopping.wish.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import shopping.member.domain.Member;
import shopping.member.domain.MemberRepository;
import shopping.product.domain.Product;
import shopping.product.domain.ProductRepository;
import shopping.product.exception.ProductNotFoundException;
import shopping.wish.domain.Wish;
import shopping.wish.domain.WishRepository;
import shopping.wish.dto.WishResponse;
import shopping.wish.exception.DuplicateWishException;
import shopping.wish.exception.ForbiddenWishException;
import shopping.wish.exception.WishNotFoundException;

@SpringBootTest
@Transactional
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
    private Product product;

    @BeforeEach
    void setUp() {
        member = memberRepository.save(new Member("user@test.com", "password"));
        product = productRepository.save(
                new Product("상품", 1_000L, "https://example.com/product.jpg"));
    }

    @Test
    @DisplayName("위시 리스트에 상품을 추가한다")
    void add() {
        WishResponse response = wishService.add(member.getId(), product.getId());

        assertThat(response.productId()).isEqualTo(product.getId());
        assertThat(wishRepository.existsById(response.id())).isTrue();
    }

    @Test
    @DisplayName("존재하지 않는 상품을 위시에 추가하면 예외가 발생한다")
    void addUnknownProduct() {
        assertThatThrownBy(() -> wishService.add(member.getId(), 999L))
                .isInstanceOf(ProductNotFoundException.class);
    }

    @Test
    @DisplayName("이미 위시에 있는 상품을 다시 추가하면 예외가 발생한다")
    void addDuplicateProduct() {
        wishService.add(member.getId(), product.getId());

        assertThatThrownBy(() -> wishService.add(member.getId(), product.getId()))
                .isInstanceOf(DuplicateWishException.class)
                .hasMessage("이미 위시 리스트에 등록된 상품입니다.");
    }

    @Test
    @DisplayName("회원의 위시 리스트에 등록된 상품만 조회한다")
    void findAll() {
        Member other = memberRepository.save(new Member("other@test.com", "password"));
        wishService.add(member.getId(), product.getId());
        wishService.add(other.getId(), product.getId());

        List<WishResponse> responses = wishService.findAll(member.getId());

        assertThat(responses).hasSize(1);
        assertThat(responses.getFirst().productId()).isEqualTo(product.getId());
    }

    @Test
    @DisplayName("위시 리스트에서 상품을 삭제한다")
    void delete() {
        Wish wish = wishRepository.save(new Wish(member, product));

        wishService.delete(member.getId(), wish.getId());

        assertThat(wishRepository.existsById(wish.getId())).isFalse();
    }

    @Test
    @DisplayName("존재하지 않는 위시를 삭제하면 예외가 발생한다")
    void deleteUnknownWish() {
        assertThatThrownBy(() -> wishService.delete(member.getId(), 999L))
                .isInstanceOf(WishNotFoundException.class)
                .hasMessage("존재하지 않는 위시입니다.");
    }

    @Test
    @DisplayName("다른 회원의 위시를 삭제하면 예외가 발생한다")
    void deleteOtherMembersWish() {
        Member other = memberRepository.save(new Member("other@test.com", "password"));
        Wish wish = wishRepository.save(new Wish(other, product));

        assertThatThrownBy(() -> wishService.delete(member.getId(), wish.getId()))
                .isInstanceOf(ForbiddenWishException.class)
                .hasMessage("다른 회원의 위시에 접근할 수 없습니다.");
    }
}
