package shopping.domains.user.core.domain.entity;

import lombok.NonNull;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import shopping.domains.user.core.domain.dto.UserDto;
import shopping.domains.user.test.util.TestEncryptUtil;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;
import static shopping.domains.user.test.fixture.UserTestFixture.*;

class UserTest {
    @Test
    @DisplayName("회원은 이메일, 비밀번호를 가진다.")
    void constructorTest() {
        // given
        final EncryptedEmail encryptedEmail = new EncryptedEmail(TestEncryptUtil.encrypt(EMAILS.get(0)));
        final EncryptedPassword encryptedPassword = new EncryptedPassword(ENCRYPTED_PASSWORDS.get(0));

        // when & then
        assertThatNoException().isThrownBy(() -> new User(encryptedEmail, encryptedPassword));
    }

    @Test
    @DisplayName("회원이 올바른 비밀번호를 보내면 토큰을 발급한다.")
    void signInTest() {
        // given
        final EncryptedEmail encryptedEmail = new EncryptedEmail(TestEncryptUtil.encrypt(EMAILS.get(0)));
        final RawPassword rawPassword = new RawPassword(RAW_PASSWORDS.get(0));
        final EncryptedPassword encryptedPassword = new EncryptedPassword(ENCRYPTED_PASSWORDS.get(0));
        final User user = new User(encryptedEmail, encryptedPassword);
        final AuthToken token = AuthToken.builder()
                .accessToken("accessToken")
                .refreshToken("refreshToken")
                .build();
        final HashStrategy hashStrategy = new HashStrategy() {
            @Override
            public @NonNull String encrypt(@NonNull String origin) {
                return null;
            }

            @Override
            public boolean match(
                    @NonNull final String origin,
                    @NonNull final String encrypted
            ) {
                return true;
            }
        };

        // when
        final AuthToken actual = user.signIn(rawPassword, hashStrategy, token);

        // then
        assertThat(actual).isNotNull();
    }

    @ParameterizedTest
    @DisplayName("회원 생성자 NPE 테스트")
    @MethodSource("constructorNullParameters")
    void constructorNPETest(
            final EncryptedEmail encryptedEmail,
            final EncryptedPassword encryptedPassword
    ) {
        assertThatNullPointerException().isThrownBy(() -> User.builder()
                .email(encryptedEmail)
                .password(encryptedPassword)
                .build());
    }

    private static Stream<Arguments> constructorNullParameters() {
        // given
        final EncryptedEmail encryptedEmail = new EncryptedEmail(TestEncryptUtil.encrypt(EMAILS.get(0)));
        final EncryptedPassword encryptedPassword = new EncryptedPassword(ENCRYPTED_PASSWORDS.get(0));

        return Stream.of(
                Arguments.of(null, encryptedPassword),
                Arguments.of(encryptedEmail, null)
        );
    }

    @Test
    @DisplayName("Dto 생성자 테스트")
    void dtoConstructorTest() {
        // given
        final UserDto dto = UserDto.builder()
                .encryptedEmail("encryptedEmail")
                .encryptedPassword("encryptedPassword")
                .build();

        // when & then
        assertThatNoException().isThrownBy(() -> new User(dto));
    }

    @Test
    @DisplayName("toDto 테스트")
    void toDtoTest() {
        // given
        final UserDto expected = UserDto.builder()
                .encryptedEmail("encryptedEmail")
                .encryptedPassword("encryptedPassword")
                .build();
        final User user = new User(expected);

        // when
        final UserDto actual = user.toDto();

        // then
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("회원이 올바르지 않은 비밀번호를 보내면 예외가 발생한다.")
    void signInExceptionTest() {
        // given
        final EncryptedEmail encryptedEmail = new EncryptedEmail(TestEncryptUtil.encrypt(EMAILS.get(0)));
        final RawPassword rawPassword = new RawPassword(RAW_PASSWORDS.get(0));
        final EncryptedPassword encryptedPassword = new EncryptedPassword(ENCRYPTED_PASSWORDS.get(0));
        final User user = new User(encryptedEmail, encryptedPassword);
        final AuthToken token = AuthToken.builder()
                .accessToken("accessToken")
                .refreshToken("refreshToken")
                .build();
        final HashStrategy hashStrategy = new HashStrategy() {
            @Override
            public @NonNull String encrypt(@NonNull String origin) {
                return null;
            }

            @Override
            public boolean match(
                    @NonNull final String origin,
                    @NonNull final String encrypted
            ) {
                return false;
            }
        };

        // when & then
        assertThatIllegalArgumentException().isThrownBy(() -> user.signIn(rawPassword, hashStrategy, token));
    }
}
