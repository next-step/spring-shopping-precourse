package shopping.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ProductTest {
    private static final ProductName NAME = new ProductName("상품");

    @Test
    @DisplayName("상품 이름은 null일 수 없다")
    void rejectNullName() {
        assertThatThrownBy(() -> new Product(null, 1_000L, "image-url"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("상품 가격과 이미지 URL은 null일 수 없다")
    void rejectNullFields() {
        assertThatThrownBy(() -> new Product(NAME, null, "image-url"))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new Product(NAME, 1_000L, null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("잘못된 값으로 상품을 수정하면 기존 값이 유지된다")
    void keepValuesWhenUpdateFails() {
        Product product = new Product(NAME, 1_000L, "image-url");

        assertThatThrownBy(() -> product.update(new ProductName("수정"), null, "new-image"))
                .isInstanceOf(IllegalArgumentException.class);
        assertThat(product.getName()).isEqualTo("상품");
    }
}
