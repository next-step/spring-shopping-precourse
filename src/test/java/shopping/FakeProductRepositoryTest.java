package shopping;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class FakeProductRepositoryTest {
    private final ProductRepository repository = new FakeProductRepository();

    @Test
    void 등록한_상품을_ID로_조회한다() {
        var product = repository.registeredProduct("치이카와", 1000L, "www.example.com");

        Assertions.assertThat(product.id()).isEqualTo(1L);
        Assertions.assertThat(repository.product(product.id())).isEqualTo(product);
    }

    @Test
    void 목록은_상품을_등록한_순서대로_반환한다() {
        var first = repository.registeredProduct("치이카와", 1000L, "www.example.com");
        var second = repository.registeredProduct("하치와레", 2000L, "www.example.com");

        Assertions.assertThat(repository.products()).containsExactly(first, second);
    }

    @Test
    void 상품을_수정해도_ID와_등록_순서는_유지된다() {
        var first = repository.registeredProduct("치이카와", 1000L, "www.example.com");
        var second = repository.registeredProduct("하치와레", 2000L, "www.example.com");
        var updated = new Product(first.id(), "우사기", 3000L, "www.example.com/usagi");

        repository.update(first.id(), updated.name(), updated.price(), updated.url());

        Assertions.assertThat(repository.product(first.id())).isEqualTo(updated);
        Assertions.assertThat(repository.products()).containsExactly(updated, second);
    }

    @Test
    void 잘못된_가격으로_수정하면_기존_상품을_유지한다() {
        var product = repository.registeredProduct("치이카와", 1000L, "www.example.com");

        Assertions.assertThatThrownBy(
                () -> repository.update(product.id(), "우사기", -1L, "www.example.com"))
                .isInstanceOf(IllegalArgumentException.class);
        Assertions.assertThat(repository.product(product.id())).isEqualTo(product);
    }

    @Test
    void 삭제한_상품은_조회할_수_없다() {
        var product = repository.registeredProduct("치이카와", 1000L, "www.example.com");

        repository.remove(product.id());

        Assertions.assertThat(repository.product(product.id())).isNull();
        Assertions.assertThat(repository.products()).isEmpty();
    }

    // 미등록 ID로 수정하거나 삭제하면 예외가 발생하는지도 테스트할 수 있다. 생략.
    // 미등록 ID 조회와 등록 실패 시 상품 목록과 다음 ID가 유지되는지도 테스트할 수 있다. 생략.
}
