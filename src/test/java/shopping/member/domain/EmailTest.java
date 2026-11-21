package shopping.member.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

@DisplayName("이메일")
class EmailTest {

    @DisplayName("이메일은 앞뒤 공백을 제거하고 대소문자를 구분해 비교한다")
    @Test
    void comparesTrimmedAddressesWithoutIgnoringCase() {
        Email email = new Email(" Yeji@example.com ");

        assertThat(email).isEqualTo(new Email("Yeji@example.com"));
        assertThat(email).isNotEqualTo(new Email("yeji@example.com"));
    }

    @DisplayName("누락되거나 형식이 잘못된 이메일은 거절한다")
    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "user", "@example.com", "user@", "u ser@example.com"})
    void rejectsMissingOrMalformedAddress(String value) {
        assertThatIllegalArgumentException().isThrownBy(() -> new Email(value));
    }

    @DisplayName("앞뒤 공백을 제거한 이메일은 254자까지만 허용한다")
    @Test
    void limitsNormalizedAddressTo254Characters() {
        String prefix = "a".repeat(64) + "@" + "b".repeat(63) + "." + "c".repeat(63) + ".";
        String address = prefix + "d".repeat(254 - prefix.length());

        assertThat(new Email(" " + address + " ").value()).isEqualTo(address);
        assertThatIllegalArgumentException().isThrownBy(() -> new Email(address + "d"));
    }
}
