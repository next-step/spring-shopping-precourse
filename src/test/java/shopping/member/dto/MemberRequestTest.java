package shopping.member.dto;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class MemberRequestTest {

    // 스프링 없이 Bean Validation 검증기만 만들어서 MemberRequest의 규칙을 확인한다.
    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @ParameterizedTest
    @ValueSource(strings = {"user@test.com", "user.name+tag@sub.domain.co.kr"})
    @DisplayName("올바른 형식의 이메일이면 검증을 통과한다")
    void validEmail(String email) {
        Set<ConstraintViolation<MemberRequest>> violations =
                validator.validate(new MemberRequest(email, "password"));

        assertThat(violations).isEmpty();
    }

    @ParameterizedTest
    @ValueSource(strings = {"user", "user@", "@test.com", "user@test", "user@test.c", "us er@test.com"})
    @DisplayName("이메일 형식이 올바르지 않으면 검증에 실패한다")
    void invalidEmail(String email) {
        Set<ConstraintViolation<MemberRequest>> violations =
                validator.validate(new MemberRequest(email, "password"));

        assertThat(violations).extracting(ConstraintViolation::getMessage)
                .containsExactly("이메일 형식이 올바르지 않습니다.");
    }

    // @NullAndEmptySource: null과 ""를, @ValueSource: 공백만 있는 문자열을 넣어 본다.
    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "   "})
    @DisplayName("이메일이 비어 있으면 검증에 실패한다")
    void blankEmail(String email) {
        Set<ConstraintViolation<MemberRequest>> violations =
                validator.validate(new MemberRequest(email, "password"));

        // 공백 문자열은 형식 검사(@Email)에도 걸릴 수 있으므로 contains로 확인한다.
        assertThat(violations).extracting(ConstraintViolation::getMessage)
                .contains("이메일은 비어 있을 수 없습니다.");
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "   "})
    @DisplayName("비밀번호가 비어 있으면 검증에 실패한다")
    void blankPassword(String password) {
        Set<ConstraintViolation<MemberRequest>> violations =
                validator.validate(new MemberRequest("user@test.com", password));

        assertThat(violations).extracting(ConstraintViolation::getMessage)
                .containsExactly("비밀번호는 비어 있을 수 없습니다.");
    }
}
