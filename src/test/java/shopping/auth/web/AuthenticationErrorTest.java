package shopping.auth.web;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import shopping.common.error.ErrorCode;
import shopping.common.error.ShoppingException;
import shopping.common.web.ErrorHandler;

@DisplayName("인증 오류 응답")
class AuthenticationErrorTest {

    @DisplayName("인증 실패 응답에 Bearer 인증 헤더를 포함한다")
    @Test
    void authenticationFailureReturnsBearerChallenge() {
        var response = new ErrorHandler().handleShopping(
                new ShoppingException(ErrorCode.INVALID_TOKEN));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getHeaders().getFirst(HttpHeaders.WWW_AUTHENTICATE))
                .isEqualTo("Bearer");
    }
}
