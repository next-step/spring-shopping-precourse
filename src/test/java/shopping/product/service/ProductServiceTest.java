package shopping.product.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;
import shopping.common.exception.BusinessException;
import shopping.common.exception.ErrorCode;
import shopping.product.dto.ProductRequest;
import shopping.product.dto.ProductResponse;
import shopping.product.profanity.ProfanityChecker;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

@SpringBootTest
@Transactional
class ProductServiceTest {

    @Autowired
    ProductService productService;

    @MockitoBean
    ProfanityChecker profanityChecker;

    private ProductRequest request(String name) {
        return new ProductRequest(name, 1000, 10, "https://i.ibb.co/a.jpg");
    }

    @Test
    void 상품_등록() {
        ProductResponse response = productService.createProduct(request("돼지뚱냥이"));

        assertThat(response.id()).isNotNull();
        assertThat(response.name()).isEqualTo("돼지뚱냥이");
    }

    @Test
    void 이름_중복이면_예외() {
        productService.createProduct(request("돼지뚱냥이"));

        assertThatThrownBy(() -> productService.createProduct(request("돼지뚱냥이")))
                .isInstanceOf(BusinessException.class)
                .extracting(e -> ((BusinessException) e).getErrorCode())
                .isEqualTo(ErrorCode.DUPLICATE_PRODUCT);
    }

    @Test
    void 비속어가_포함되면_예외() {
        given(profanityChecker.containsProfanity(anyString())).willReturn(true);

        assertThatThrownBy(() -> productService.createProduct(request("fuck")))
                .isInstanceOf(BusinessException.class)
                .extracting(e -> ((BusinessException) e).getErrorCode())
                .isEqualTo(ErrorCode.PRODUCT_NAME_PROFANITY);
    }

    @Test
    void 없는_상품_조회시_예외() {
        assertThatThrownBy(() -> productService.getProduct(999L))
                .isInstanceOf(BusinessException.class)
                .extracting(e -> ((BusinessException) e).getErrorCode())
                .isEqualTo(ErrorCode.PRODUCT_NOT_FOUND);
    }

    @Test
    void 상품_수정() {
        ProductResponse saved = productService.createProduct(request("돼지뚱냥이"));

        ProductResponse updated = productService.updateProduct(saved.id(),
                new ProductRequest("뚱냥이", 2000, 3, "https://i.ibb.co/b.jpg"));

        assertThat(updated.name()).isEqualTo("뚱냥이");
        assertThat(updated.price()).isEqualTo(2000);
    }

    @Test
    void 자기_이름으로_수정은_허용() {
        ProductResponse saved = productService.createProduct(request("돼지뚱냥이"));

        ProductResponse updated = productService.updateProduct(saved.id(),
                new ProductRequest("돼지뚱냥이", 2000, 3, "https://i.ibb.co/b.jpg"));

        assertThat(updated.price()).isEqualTo(2000);
    }

    @Test
    void 다른_상품_이름으로_수정시_예외() {
        productService.createProduct(request("돼지뚱냥이"));
        ProductResponse other = productService.createProduct(request("뚱냥이"));

        assertThatThrownBy(() -> productService.updateProduct(other.id(), request("돼지뚱냥이")))
                .isInstanceOf(BusinessException.class)
                .extracting(e -> ((BusinessException) e).getErrorCode())
                .isEqualTo(ErrorCode.DUPLICATE_PRODUCT);
    }

    @Test
    void 상품_삭제() {
        ProductResponse saved = productService.createProduct(request("돼지뚱냥이"));

        productService.deleteProduct(saved.id());

        assertThatThrownBy(() -> productService.getProduct(saved.id()))
                .isInstanceOf(BusinessException.class);
    }
}
