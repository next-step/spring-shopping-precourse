package shopping.dto;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

class MemberRequestTest {
    private static final ValidatorFactory FACTORY =
            Validation.buildDefaultValidatorFactory();
    private static final Validator VALIDATOR = FACTORY.getValidator();

    @AfterAll
    static void closeFactory() {
        FACTORY.close();
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "userexample.com", "user@", "@example.com"})
    @DisplayName("이메일은 필수이며 이메일 형식이어야 한다")
    void rejectInvalidEmail(String email) {
        MemberRequest request = new MemberRequest(email, "password");

        assertThat(VALIDATOR.validateProperty(request, "email")).isNotEmpty();
    }

    @ParameterizedTest
    @NullSource
    @DisplayName("비밀번호는 null일 수 없다")
    void rejectNullPassword(String password) {
        MemberRequest request = new MemberRequest("user@example.com", password);

        assertThat(VALIDATOR.validateProperty(request, "password")).isNotEmpty();
    }

    @Test
    @DisplayName("유효한 회원 입력은 검증을 통과한다")
    void acceptValidMemberRequest() {
        MemberRequest request = new MemberRequest("user@example.com", "password");

        assertThat(VALIDATOR.validate(request)).isEmpty();
    }
}
