package shopping;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.stream.Stream;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class MemberRequestTest {

    private static ValidatorFactory factory;
    private static Validator validator;

    @BeforeAll
    static void setUp() {
        factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @AfterAll
    static void closeValidatorFactory() {
        factory.close();
    }

    @ParameterizedTest
    @MethodSource("invalidCredentials")
    void rejectsInvalidRegisterAndLoginRequests(String email, String password) {
        assertThat(validator.validate(new MemberRegisterRequest(email, password))).isNotEmpty();
        assertThat(validator.validate(new MemberLoginRequest(email, password))).isNotEmpty();
    }

    static Stream<Arguments> invalidCredentials() {
        return Stream.of(
                Arguments.of(null, "password123!"),
                Arguments.of("   ", "password123!"),
                Arguments.of("not-an-email", "password123!"),
                Arguments.of("a".repeat(250) + "@example.com", "password123!"),
                Arguments.of("member@example.com", null),
                Arguments.of("member@example.com", "        "),
                Arguments.of("member@example.com", "short"),
                Arguments.of("member@example.com", "a".repeat(65)),
                Arguments.of("member@example.com", "가".repeat(25)));
    }

    @ParameterizedTest
    @MethodSource("validPasswords")
    void acceptsPasswordBoundariesWithoutTrimming(String password) {
        MemberRegisterRequest register = new MemberRegisterRequest("member@example.com", password);
        MemberLoginRequest login = new MemberLoginRequest("member@example.com", password);
        assertThat(validator.validate(register)).isEmpty();
        assertThat(validator.validate(login)).isEmpty();
        assertThat(register.password()).isEqualTo(password);
        assertThat(login.password()).isEqualTo(password);
    }

    static Stream<String> validPasswords() {
        return Stream.of("password", "a".repeat(64), "가".repeat(24), " password123! ");
    }

    @Test
    void normalizesEmailForBothRequests() {
        String email = "  Member@Example.COM  ";
        assertThat(new MemberRegisterRequest(email, "password123!").email())
                .isEqualTo("member@example.com");
        assertThat(new MemberLoginRequest(email, "password123!").email())
                .isEqualTo("member@example.com");
    }
}
