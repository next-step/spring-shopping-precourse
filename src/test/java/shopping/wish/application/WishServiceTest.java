package shopping.wish.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import shopping.common.error.ErrorCode;
import shopping.common.error.ShoppingException;
import shopping.product.domain.Product;
import shopping.product.persistence.ProductRepository;
import shopping.wish.domain.Wish;
import shopping.wish.persistence.WishRepository;

@DisplayName("위시 서비스")
class WishServiceTest {

    private final WishRepository wishes = mock(WishRepository.class);
    private final ProductRepository products = mock(ProductRepository.class);
    private final WishService service = new WishService(wishes, products);

    @DisplayName("회원별 중복 여부를 확인한 뒤 존재하는 상품을 위시에 추가한다")
    @Test
    void addsExistingProductAfterMemberScopedDuplicateCheck() {
        when(products.findById(9L)).thenReturn(Optional.of(mock(Product.class)));
        Wish saved = mock(Wish.class);
        when(wishes.saveAndFlush(any(Wish.class))).thenReturn(saved);

        assertThat(service.add(7L, 9L)).isSameAs(saved);
        verify(wishes).existsByMemberIdAndProduct_Id(7L, 9L);
    }

    @DisplayName("추가할 상품이 없으면 위시 저장소에 접근하지 않는다")
    @Test
    void missingProductDoesNotAccessWishlist() {
        when(products.findById(9L)).thenReturn(Optional.empty());

        assertAdditionFailure(ErrorCode.PRODUCT_NOT_FOUND);
        verifyNoInteractions(wishes);
    }

    @DisplayName("회원이 이미 담은 상품은 위시에 다시 저장하지 않는다")
    @Test
    void duplicateWishDoesNotSave() {
        when(products.findById(9L)).thenReturn(Optional.of(mock(Product.class)));
        when(wishes.existsByMemberIdAndProduct_Id(7L, 9L)).thenReturn(true);

        assertAdditionFailure(ErrorCode.WISH_ALREADY_EXISTS);
        verify(wishes, never()).saveAndFlush(any(Wish.class));
    }

    @DisplayName("요청한 회원의 위시 목록을 조회한다")
    @Test
    void retrievesWishesForRequestedMember() {
        Wish wish = mock(Wish.class);
        when(wishes.findAllWithProducts(7L)).thenReturn(List.of(wish));

        assertThat(service.list(7L)).containsExactly(wish);
    }

    @DisplayName("위시 도메인의 소유자 확인을 거친 뒤 삭제한다")
    @Test
    void deletesAfterDomainOwnershipCheck() {
        Wish wish = mock(Wish.class);
        when(wishes.findById(9L)).thenReturn(Optional.of(wish));

        service.delete(7L, 9L);

        verify(wish).requireOwner(7L);
        verify(wishes).delete(wish);
    }

    @DisplayName("소유자 확인에 실패하면 위시 항목을 삭제하지 않는다")
    @Test
    void domainRejectionPreventsDeletion() {
        Wish wish = mock(Wish.class);
        when(wishes.findById(9L)).thenReturn(Optional.of(wish));
        doThrow(new ShoppingException(ErrorCode.FORBIDDEN)).when(wish).requireOwner(7L);

        assertDeletionFailure(ErrorCode.FORBIDDEN);
        verify(wishes, never()).delete(any(Wish.class));
    }

    @DisplayName("위시 항목이 없으면 오류를 반환하고 삭제하지 않는다")
    @Test
    void missingWishPreventsDeletion() {
        when(wishes.findById(9L)).thenReturn(Optional.empty());

        assertDeletionFailure(ErrorCode.WISH_NOT_FOUND);
        verify(wishes, never()).delete(any(Wish.class));
    }

    private void assertDeletionFailure(ErrorCode code) {
        assertThatThrownBy(() -> service.delete(7L, 9L))
                .isInstanceOfSatisfying(ShoppingException.class,
                        error -> assertThat(error.code()).isEqualTo(code));
    }

    private void assertAdditionFailure(ErrorCode code) {
        assertThatThrownBy(() -> service.add(7L, 9L))
                .isInstanceOfSatisfying(ShoppingException.class,
                        error -> assertThat(error.code()).isEqualTo(code));
    }
}
