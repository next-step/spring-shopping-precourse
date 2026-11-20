package shopping;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class FakeWishRepositoryTest {
    private final WishRepository repository = new FakeWishRepository();

    @Test
    void 같은_상품을_다시_담으면_수량이_늘어난다() {
        var first = repository.registered(1L, 10L);
        var second = repository.registered(1L, 10L);

        Assertions.assertThat(second.id()).isEqualTo(first.id());
        Assertions.assertThat(second.quantity()).isEqualTo(2);
        Assertions.assertThat(repository.wishes(1L)).containsExactly(second);
    }

    @Test
    void 같은_상품도_회원마다_수량을_따로_관리한다() {
        repository.registered(1L, 10L);
        var other = repository.registered(2L, 10L);
        var increased = repository.registered(1L, 10L);

        Assertions.assertThat(other.quantity()).isEqualTo(1);
        Assertions.assertThat(repository.wishes(1L)).containsExactly(increased);
        Assertions.assertThat(repository.wishes(2L)).containsExactly(other);
    }

    // 수량이 늘어도 처음 담은 순서가 유지되는지 검증할 수 있다. 생략.
}
