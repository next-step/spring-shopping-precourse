package shopping.auth.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import shopping.common.error.ErrorCode;
import shopping.common.error.ShoppingException;

@DisplayName("Bearer 인증 헤더")
class BearerTokenTest {

    @DisplayName("Bearer 표기의 대소문자와 관계없이 토큰을 추출한다")
    @Test
    void extractsTokenIgnoringSchemeCase() {
        assertThat(BearerToken.parse("bEaReR random-token")).isEqualTo("random-token");
    }

    @DisplayName("누락되거나 형식이 잘못된 인증 헤더는 거절한다")
    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"Basic abc", "Bearer", "Bearer ", "Bearer a b"})
    void rejectsAbsentOrMalformedHeader(String header) {
        assertThatThrownBy(() -> BearerToken.parse(header))
                .isInstanceOfSatisfying(ShoppingException.class,
                        error -> assertThat(error.code()).isEqualTo(ErrorCode.INVALID_TOKEN));
    }
}
