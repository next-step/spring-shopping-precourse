package shopping.product;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

class ProductNameFactoryTest {

    @Test
    void 비속어가_없으면_이름이_생성된다() {
        ProductNameFactory factory = new ProductNameFactory(text -> false);

        assertThat(factory.create("사과").getValue()).isEqualTo("사과");
    }

    @Test
    void 비속어가_있으면_예외가_발생한다() {
        ProductNameFactory factory = new ProductNameFactory(text -> true);

        assertThatThrownBy(() -> factory.create("사과"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("비속어");
    }

    @Test
    void 형식이_잘못되면_비속어_검사_전에_예외가_발생한다() {
        ProductNameFactory factory = new ProductNameFactory(text -> {
            throw new AssertionError("호출되면 안 됩니다");
        });

        assertThatThrownBy(() -> factory.create("사과@"))
                .isInstanceOf(IllegalArgumentException.class);
    }
}