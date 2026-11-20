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

    // 미등록 ID 조회와 등록 실패 시 상품 목록과 다음 ID가 유지되는지도 테스트할 수 있다. 생략.
}
