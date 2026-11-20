package shopping.common;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void 외부_서비스_오류는_503과_오류_메시지로_바꾼다() {
        ExternalServiceException exception =
                new ExternalServiceException("비속어 검사 서비스를 이용할 수 없습니다.", new RuntimeException());

        ResponseEntity<ErrorResponse> response = handler.handleExternalService(exception);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.SERVICE_UNAVAILABLE);
        assertThat(response.getBody().message()).isEqualTo("비속어 검사 서비스를 이용할 수 없습니다.");
    }
}
