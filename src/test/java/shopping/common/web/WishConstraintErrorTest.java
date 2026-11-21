package shopping.common.web;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.dao.DataIntegrityViolationException;

@DisplayName("위시 저장 제약 오류 응답")
class WishConstraintErrorTest {

    @DisplayName("위시 저장 제약 위반을 명세에 정의된 오류로 변환한다")
    @ParameterizedTest
    @CsvSource({"UQ_WISHES_MEMBER_PRODUCT,409,WISH_ALREADY_EXISTS",
            "FK_WISHES_PRODUCT,404,PRODUCT_NOT_FOUND"})
    void translatesStorageConflictsToDocumentedErrors(String constraint, int status, String code) {
        var response = new ErrorHandler().handleConstraint(
                new DataIntegrityViolationException(constraint));

        assertThat(response.getStatusCode().value()).isEqualTo(status);
        assertThat(response.getBody()).isInstanceOfSatisfying(ErrorResponse.class,
                body -> assertThat(body.code()).isEqualTo(code));
    }
}
