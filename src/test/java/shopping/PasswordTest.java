package shopping;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class PasswordTest {

    @Test
    void 비밀번호_길이가_10자_미만이면_불가능() {
        Assertions.assertThatThrownBy(() -> new Password("123456789"))
                .isInstanceOf(IllegalArgumentException.class);
    }
}