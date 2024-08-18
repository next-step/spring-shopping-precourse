package shopping.domains.user.core.domain.entity;

import lombok.NonNull;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;
import static shopping.domains.user.test.fixture.UserTestFixture.INVALID_RAW_PASSWORDS;
import static shopping.domains.user.test.fixture.UserTestFixture.RAW_PASSWORDS;

class RawPasswordTest {
    @ParameterizedTest
    @DisplayName("비밀번호는 8자 이상, 20자 이하이면서 알파벳 대소문자, 숫자, 특수문자 하나씩 포함해야만 한다.")
    @MethodSource("rawPasswordParameters")
    void rawPasswordConstructorTest(@NonNull final String rawPassword) {
        assertThatNoException().isThrownBy(() -> new RawPassword(rawPassword));
    }

    private static Stream<Arguments> rawPasswordParameters() {
        return RAW_PASSWORDS.stream()
                .map(Arguments::of);
    }

    @ParameterizedTest
    @DisplayName("비밀번호는 비밀번호 형식이 아닐시 예외가 발생한다.")
    @MethodSource("invalidRawPasswordParameters")
    void rawPasswordInvalidConstructorTest(@NonNull final String rawPassword) {
        assertThatIllegalArgumentException().isThrownBy(() -> new RawPassword(rawPassword));
    }

    private static Stream<Arguments> invalidRawPasswordParameters() {
        return INVALID_RAW_PASSWORDS.stream()
                .map(Arguments::of);
    }

    @Test
    @DisplayName("값이 null인 경우 NPE를 발생시킨다.")
    void constructorNPETest() {
        assertThatNullPointerException().isThrownBy(() -> new RawPassword(null));
    }
}
