package shopping.domains.user.core.domain.entity;

import lombok.NonNull;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;
import static shopping.domains.user.test.fixture.UserTestFixture.EMAILS;
import static shopping.domains.user.test.fixture.UserTestFixture.INVALID_EMAILS;

class EmailTest {
    @ParameterizedTest
    @DisplayName("이메일은 형식을 지켜야 한다.")
    @MethodSource("constructorParameters")
    void constructorTest(@NonNull final String email) {
        assertThatNoException().isThrownBy(() -> new RawEmail(email));
    }

    private static Stream<Arguments> constructorParameters() {
        return EMAILS.stream()
                .map(Arguments::of);
    }

    @Test
    @DisplayName("이메일 값이 null인 경우 NPE를 발생시킨다.")
    void constructorNPETest() {
        assertThatNullPointerException().isThrownBy(() -> new RawEmail(null));
    }

    @ParameterizedTest
    @DisplayName("이메일 값이 잘못된 경우 예외를 발생시킨다.")
    @MethodSource("constructorInvalidParameters")
    void constructorExceptionTest(@NonNull final String email) {
        assertThatIllegalArgumentException().isThrownBy(() -> new RawEmail(email));
    }

    private static Stream<Arguments> constructorInvalidParameters() {
        return INVALID_EMAILS.stream()
                .map(Arguments::of);
    }

    @ParameterizedTest
    @DisplayName("equalsAndHashCode 테스트")
    @MethodSource("equalsAndHashCodeParameters")
    void equalsAndHashCodeTest(
            @NonNull final String left,
            @NonNull final String right
    ) {
        // given
        final RawEmail expected = new RawEmail(left);

        // when
        final RawEmail actual = new RawEmail(right);

        // then
        assertThat(actual).isEqualTo(expected);
        assertThat(actual).isNotSameAs(expected);
    }

    private static Stream<Arguments> equalsAndHashCodeParameters() {
        return EMAILS.stream()
                .map(email -> new String[]{email, email})
                .map(Arguments::of);
    }
}
