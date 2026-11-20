package shopping.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import shopping.entity.Product;
import shopping.external.ProfanityChecker;
import shopping.repository.ProductRepository;

import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ProductServiceTests {

    private static final String IMAGE_URL = "https://api.dicebear.com/10.x/waves/svg";

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProfanityChecker profanityChecker;

    @InjectMocks
    private ProductService productService;

    @DisplayName("비속어가 없는 상품 이름이면 상품을 저장한다")
    @Test
    void createSavesProduct() {
        given(profanityChecker.containsProfanity("치킨")).willReturn(false);
        given(productRepository.save(any(Product.class))).willAnswer(invocation -> invocation.getArgument(0));

        Product created = productService.create("치킨", 20000, IMAGE_URL);

        assertThat(created.getName()).isEqualTo("치킨");
        verify(productRepository).save(any(Product.class));
    }

    @DisplayName("상품 이름에 비속어가 있으면 예외가 발생하고 저장하지 않는다")
    @Test
    void createWithProfanity() {
        given(profanityChecker.containsProfanity("fuck")).willReturn(true);

        assertThatThrownBy(() -> productService.create("fuck", 20000, IMAGE_URL))
                .isInstanceOf(IllegalArgumentException.class);
        verify(productRepository, never()).save(any());
    }

    @DisplayName("id로 상품을 조회한다")
    @Test
    void findById() {
        Product product = Product.of("치킨", 20000, IMAGE_URL);
        given(productRepository.findById(1L)).willReturn(Optional.of(product));

        Product found = productService.findById(1L);

        assertThat(found).isSameAs(product);
    }

    @DisplayName("존재하지 않는 id로 조회하면 예외가 발생한다")
    @Test
    void findByIdNotFound() {
        given(productRepository.findById(1L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> productService.findById(1L))
                .isInstanceOf(NoSuchElementException.class);
    }

    @DisplayName("상품을 수정하면 이름, 가격, 이미지 URL이 변경된다")
    @Test
    void updateChangesProduct() {
        Product product = Product.of("치킨", 20000, IMAGE_URL);
        given(productRepository.findById(1L)).willReturn(Optional.of(product));
        given(profanityChecker.containsProfanity("피자")).willReturn(false);

        Product updated = productService.update(1L, "피자", 25000, "https://example.com/b.svg");

        assertThat(updated.getName()).isEqualTo("피자");
        assertThat(updated.getPrice()).isEqualTo(25000);
        assertThat(updated.getImageUrl()).isEqualTo("https://example.com/b.svg");
    }

    @DisplayName("수정할 상품 이름에 비속어가 있으면 예외가 발생한다")
    @Test
    void updateWithProfanity() {
        Product product = Product.of("치킨", 20000, IMAGE_URL);
        given(productRepository.findById(1L)).willReturn(Optional.of(product));
        given(profanityChecker.containsProfanity("fuck")).willReturn(true);

        assertThatThrownBy(() -> productService.update(1L, "fuck", 25000, IMAGE_URL))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("존재하지 않는 id의 상품을 수정하면 예외가 발생한다")
    @Test
    void updateNotFound() {
        given(productRepository.findById(1L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> productService.update(1L, "피자", 25000, IMAGE_URL))
                .isInstanceOf(NoSuchElementException.class);
        verify(profanityChecker, never()).containsProfanity(any());
    }

    @DisplayName("상품을 삭제한다")
    @Test
    void deleteProduct() {
        given(productRepository.existsById(1L)).willReturn(true);

        productService.delete(1L);

        verify(productRepository).deleteById(1L);
    }

    @DisplayName("존재하지 않는 id의 상품을 삭제하면 예외가 발생한다")
    @Test
    void deleteNotFound() {
        given(productRepository.existsById(1L)).willReturn(false);

        assertThatThrownBy(() -> productService.delete(1L))
                .isInstanceOf(NoSuchElementException.class);
        verify(productRepository, never()).deleteById(any());
    }
}
