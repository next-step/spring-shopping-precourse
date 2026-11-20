package shopping.wish.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import shopping.common.exception.BusinessException;
import shopping.common.exception.ErrorCode;
import shopping.product.entity.Product;
import shopping.product.repository.ProductRepository;
import shopping.wish.dto.WishResponse;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class WishServiceTest {

    @Autowired WishService wishService;
    @Autowired ProductRepository productRepository;

    private Long saveProduct() {
        return productRepository.save(new Product("돼지뚱냥이", 1000, 1, "https://i.ibb.co/a.jpg")).getId();
    }

    @Test
    void 위시_추가() {
        Long productId = saveProduct();

        WishResponse response = wishService.addWish(1L, productId);

        assertThat(response.productId()).isEqualTo(productId);
        assertThat(wishService.getWishes(1L)).hasSize(1);
    }

    @Test
    void 없는_상품이면_예외() {
        assertThatThrownBy(() -> wishService.addWish(1L, 999L))
                .isInstanceOf(BusinessException.class)
                .extracting(e -> ((BusinessException) e).getErrorCode())
                .isEqualTo(ErrorCode.PRODUCT_NOT_FOUND);
    }

    @Test
    void 이미_담은_상품이면_예외() {
        Long productId = saveProduct();
        wishService.addWish(1L, productId);

        assertThatThrownBy(() -> wishService.addWish(1L, productId))
                .isInstanceOf(BusinessException.class)
                .extracting(e -> ((BusinessException) e).getErrorCode())
                .isEqualTo(ErrorCode.DUPLICATE_WISH);
    }

    @Test
    void 다른_회원의_위시_삭제시_예외() {
        WishResponse wish = wishService.addWish(1L, saveProduct());

        assertThatThrownBy(() -> wishService.deleteWish(2L, wish.id()))
                .isInstanceOf(BusinessException.class)
                .extracting(e -> ((BusinessException) e).getErrorCode())
                .isEqualTo(ErrorCode.WISH_FORBIDDEN);
    }

    @Test
    void 없는_위시_삭제시_예외() {
        assertThatThrownBy(() -> wishService.deleteWish(1L, 999L))
                .isInstanceOf(BusinessException.class)
                .extracting(e -> ((BusinessException) e).getErrorCode())
                .isEqualTo(ErrorCode.WISH_NOT_FOUND);
    }

    @Test
    void 위시_삭제() {
        WishResponse wish = wishService.addWish(1L, saveProduct());

        wishService.deleteWish(1L, wish.id());

        assertThat(wishService.getWishes(1L)).isEmpty();
    }
}
