package shopping;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;


class DomainTest {

    @Test
    void 이상한_이메일() {
        Assertions.assertThatThrownBy(() -> new Domain("abc.1"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void gmail_이메일_도메인() {
        Assertions.assertThatNoException().isThrownBy(() -> new Domain("gmail.com"));
    }

    // @ParameterizedTest 생략...
}