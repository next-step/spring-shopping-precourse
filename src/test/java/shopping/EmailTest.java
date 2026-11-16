package shopping;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class EmailTest {
    @Test
    void 전체_메일_주소() {
        var email = new Email("tom", new Domain("gmail.com"));
        Assertions.assertThat(email.fullAddress()).isEqualTo("tom@gmail.com");
    }

    @Test
    void 이메일의_localPart가_blank이면_안된다() {
        Assertions.assertThatThrownBy(() -> new Email("  ", new Domain("gmail.com")))
                .isInstanceOf(IllegalArgumentException.class);

    }
}