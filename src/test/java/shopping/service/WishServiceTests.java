package shopping.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.util.ReflectionTestUtils;
import shopping.entity.Member;
import shopping.entity.PasswordEncoder;
import shopping.entity.Product;
import shopping.entity.Wish;
import shopping.exception.DuplicateWishException;
import shopping.repository.MemberRepository;
import shopping.repository.ProductRepository;
import shopping.repository.WishRepository;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WishServiceTests {

    private static final Long MEMBER_ID = 1L;
    private static final Long OTHER_MEMBER_ID = 2L;
    private static final Long PRODUCT_ID = 10L;
    private static final Long WISH_ID = 100L;

    @Mock
    private WishRepository wishRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private WishService wishService;

    private Member member;
    private Product product;

    @BeforeEach
    void setUp() {
        member = memberWithId("user@example.com", MEMBER_ID);
        product = Product.of("치킨", 20000, "https://api.dicebear.com/10.x/waves/svg");
    }

    @DisplayName("위시를 추가하면 회원과 상품을 담은 위시를 저장한다")
    @Test
    void addSavesWish() {
        given(memberRepository.findById(MEMBER_ID)).willReturn(Optional.of(member));
        given(productRepository.findById(PRODUCT_ID)).willReturn(Optional.of(product));
        given(wishRepository.existsByMemberIdAndProductId(MEMBER_ID, PRODUCT_ID)).willReturn(false);
            given(wishRepository.save(any(Wish.class))).willAnswer(invocation -> invocation.getArgument(0));

        Wish wish = wishService.add(MEMBER_ID, PRODUCT_ID);

        assertThat(wish.getMember()).isSameAs(member);
        assertThat(wish.getProduct()).isSameAs(product);
        verify(wishRepository).save(any(Wish.class));
    }

    @DisplayName("이미 담은 상품이면 DuplicateWishException이 발생하고 저장하지 않는다")
    @Test
    void addDuplicateWish() {
        given(memberRepository.findById(MEMBER_ID)).willReturn(Optional.of(member));
        given(productRepository.findById(PRODUCT_ID)).willReturn(Optional.of(product));
        given(wishRepository.existsByMemberIdAndProductId(MEMBER_ID, PRODUCT_ID)).willReturn(true);

        assertThatThrownBy(() -> wishService.add(MEMBER_ID, PRODUCT_ID))
                .isInstanceOf(DuplicateWishException.class);
        verify(wishRepository, never()).save(any());
    }

    @DisplayName("저장 시 유니크 제약 조건을 위반하면 DuplicateWishException이 발생한다")
    @Test
    void addWithUniqueConstraintViolation() {
        given(memberRepository.findById(MEMBER_ID)).willReturn(Optional.of(member));
        given(productRepository.findById(PRODUCT_ID)).willReturn(Optional.of(product));
        given(wishRepository.existsByMemberIdAndProductId(MEMBER_ID, PRODUCT_ID)).willReturn(false);
        given(wishRepository.save(any(Wish.class))).willThrow(new DataIntegrityViolationException("duplicate"));

        assertThatThrownBy(() -> wishService.add(MEMBER_ID, PRODUCT_ID))
                .isInstanceOf(DuplicateWishException.class);
    }

    @DisplayName("존재하지 않는 회원이면 예외가 발생하고 저장하지 않는다")
    @Test
    void addWithUnknownMember() {
        given(memberRepository.findById(MEMBER_ID)).willReturn(Optional.empty());

        assertThatThrownBy(() -> wishService.add(MEMBER_ID, PRODUCT_ID))
                .isInstanceOf(NoSuchElementException.class);
        verify(wishRepository, never()).save(any());
    }

    @DisplayName("존재하지 않는 상품이면 예외가 발생하고 저장하지 않는다")
    @Test
    void addWithUnknownProduct() {
        given(memberRepository.findById(MEMBER_ID)).willReturn(Optional.of(member));
        given(productRepository.findById(PRODUCT_ID)).willReturn(Optional.empty());

        assertThatThrownBy(() -> wishService.add(MEMBER_ID, PRODUCT_ID))
                .isInstanceOf(NoSuchElementException.class);
        verify(wishRepository, never()).save(any());
    }

    @DisplayName("회원의 위시 목록을 조회한다")
    @Test
    void findAllByMember() {
        Wish first = Wish.of(member, product);
        Wish second = Wish.of(member, Product.of("피자", 25000, "https://api.dicebear.com/10.x/waves/svg"));
        given(wishRepository.findAllByMemberId(MEMBER_ID)).willReturn(List.of(first, second));

        List<Wish> wishes = wishService.findAll(MEMBER_ID);

        assertThat(wishes).containsExactly(first, second);
    }

    @DisplayName("자신의 위시를 삭제한다")
    @Test
    void deleteOwnWish() {
        Wish wish = Wish.of(member, product);
        given(memberRepository.findById(MEMBER_ID)).willReturn(Optional.of(member));
        given(wishRepository.findById(WISH_ID)).willReturn(Optional.of(wish));

        wishService.delete(MEMBER_ID, WISH_ID);

        verify(wishRepository).delete(wish);
    }

    @DisplayName("존재하지 않는 위시를 삭제하면 예외가 발생한다")
    @Test
    void deleteUnknownWish() {
        given(memberRepository.findById(MEMBER_ID)).willReturn(Optional.of(member));
        given(wishRepository.findById(WISH_ID)).willReturn(Optional.empty());

        assertThatThrownBy(() -> wishService.delete(MEMBER_ID, WISH_ID))
                .isInstanceOf(NoSuchElementException.class);
        verify(wishRepository, never()).delete(any());
    }

    @DisplayName("다른 회원의 위시를 삭제하면 존재하지 않는 것과 같은 예외가 발생하고 삭제하지 않는다")
    @Test
    void deleteOtherMembersWish() {
        Member otherMember = memberWithId("other@example.com", OTHER_MEMBER_ID);
        Wish wish = Wish.of(member, product);
        given(memberRepository.findById(OTHER_MEMBER_ID)).willReturn(Optional.of(otherMember));
        given(wishRepository.findById(WISH_ID)).willReturn(Optional.of(wish));

        assertThatThrownBy(() -> wishService.delete(OTHER_MEMBER_ID, WISH_ID))
                .isInstanceOf(NoSuchElementException.class);
        verify(wishRepository, never()).delete(any());
    }

    @DisplayName("없는 위시와 다른 회원의 위시를 삭제할 때의 예외 메시지가 같아 존재 여부를 알 수 없다")
    @Test
    void deleteFailureMessagesAreIdentical() {
        Member otherMember = memberWithId("other@example.com", OTHER_MEMBER_ID);
        Wish wish = Wish.of(member, product);
        given(memberRepository.findById(MEMBER_ID)).willReturn(Optional.of(member));
        given(memberRepository.findById(OTHER_MEMBER_ID)).willReturn(Optional.of(otherMember));
        given(wishRepository.findById(WISH_ID)).willReturn(Optional.empty(), Optional.of(wish));

        Throwable unknownWish = catchThrowable(() -> wishService.delete(MEMBER_ID, WISH_ID));
        Throwable otherMembersWish = catchThrowable(() -> wishService.delete(OTHER_MEMBER_ID, WISH_ID));

        assertThat(unknownWish).isInstanceOf(NoSuchElementException.class);
        assertThat(otherMembersWish).isInstanceOf(NoSuchElementException.class);
        assertThat(otherMembersWish).hasMessage(unknownWish.getMessage());
        assertThat(unknownWish.getMessage()).isNotBlank();
    }

    @DisplayName("존재하지 않는 회원이 위시를 삭제하면 예외가 발생한다")
    @Test
    void deleteWithUnknownMember() {
        given(memberRepository.findById(MEMBER_ID)).willReturn(Optional.empty());

        assertThatThrownBy(() -> wishService.delete(MEMBER_ID, WISH_ID))
                .isInstanceOf(NoSuchElementException.class);
        verify(wishRepository, never()).delete(any());
    }

    private Member memberWithId(String email, Long id) {
        Member member = Member.of(email, "password123", mock(PasswordEncoder.class));
        ReflectionTestUtils.setField(member, "id", id);
        return member;
    }
}
