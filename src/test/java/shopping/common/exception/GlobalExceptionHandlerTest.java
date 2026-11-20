package shopping.common.exception;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.test.web.servlet.assertj.MvcTestResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import shopping.auth.JwtProvider;

import static org.assertj.core.api.Assertions.assertThat;
import static shopping.support.ApiTestSupport.assertError;
import static shopping.support.ApiTestSupport.assertInvalidInput;
import static shopping.support.ApiTestSupport.postJson;

@WebMvcTest(GlobalExceptionHandlerTest.TestController.class)
@Import({GlobalExceptionHandlerTest.TestController.class,
        JwtProvider.class})
class GlobalExceptionHandlerTest {

    @Autowired
    private MockMvcTester mvc;

    @Test
    void 입력_검증에_실패하면_INVALID_INPUT과_어긴_규칙_목록을_준다() {
        // given
        String body = """
            {"name": "열여섯글자가넘는아주긴상품이름입니다","price": null}""";

        // when
        MvcTestResult result = postJson(mvc, "/test/validation", body);

        // then
        assertInvalidInput(result, "PRODUCT_NAME_TOO_LONG", "PRICE_MISSING");
    }

    @Test
    void 메시지가_ErrorCode_이름이_아니면_errors의_code는_INVALID_INPUT이다() {
        // when
        MvcTestResult result = postJson(mvc, "/test/fallback", "{}");

        // then
        assertError(result, HttpStatus.BAD_REQUEST, "INVALID_INPUT");
        assertThat(result).bodyJson().extractingPath("$.errors[0].field")
                .isEqualTo("name");
        assertThat(result).bodyJson().extractingPath("$.errors[0].code").isEqualTo("INVALID_INPUT");
    }

    @Test
    void BusinessException은_ErrorCode의_상태와_code로_응답한다() {
        // when
        MvcTestResult result = mvc.get().uri("/test/business").exchange();

        // then
        assertError(result, HttpStatus.NOT_FOUND, "NOT_EXIST_PRODUCT");
        assertThat(result).bodyJson().extractingPath("$.detail")
                .isEqualTo("존재하지 않는 상품입니다.");
    }

    @Test
    void 깨진_JSON은_INVALID_JSON이다() {
        // when
        MvcTestResult result = postJson(mvc, "/test/validation", "{");

        // then
        assertError(result, HttpStatus.BAD_REQUEST, "INVALID_JSON");
    }

    @Test
    void JSON_본문의_숫자_자리에_문자를_넣으면_INVALID_JSON이다() {
        // when
        MvcTestResult result = postJson(mvc, "/test/validation", """
                {"name": "a", "price": "abc"}
                """);

        // then
        assertError(result, HttpStatus.BAD_REQUEST, "INVALID_JSON");
    }

    @Test
    void 경로_변수_타입이_다르면_TYPE_MISMATCH이다() {
        // when
        MvcTestResult result = mvc.get().uri("/test/items/abc").exchange();

        // then
        assertError(result, HttpStatus.BAD_REQUEST, "TYPE_MISMATCH");
    }

    @Test
    void 없는_경로는_NOT_EXIST_PATH이다() {
        // when
        MvcTestResult result = mvc.get().uri("/no-such-path").exchange();

        // then
        assertError(result, HttpStatus.NOT_FOUND, "NOT_EXIST_PATH");
    }

    @Test
    void 지원하지_않는_메서드는_METHOD_NOT_ALLOWED이다() {
        // when
        MvcTestResult result = mvc.delete().uri("/test/business").exchange();

        // then
        assertError(result, HttpStatus.METHOD_NOT_ALLOWED, "METHOD_NOT_ALLOWED");
    }

    @Test
    void 지원하지_않는_형식은_UNSUPPORTED_MEDIA_TYPE이다() {
        // when
        MvcTestResult result = mvc.post().uri("/test/validation").contentType(MediaType.TEXT_PLAIN)
                .content("hello").exchange();

        // then
        assertError(result, HttpStatus.UNSUPPORTED_MEDIA_TYPE, "UNSUPPORTED_MEDIA_TYPE");
    }

    @Test
    void 처리하지_못한_예외는_INTERNAL_ERROR이다() {
        // when
        MvcTestResult result = mvc.get().uri("/test/unexpected").exchange();

        // then
        assertError(result, HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR");
        assertThat(result).bodyJson().extractingPath("$.detail")
                .isEqualTo("서버 오류가 발생했습니다.");
    }

    record TestRequest(
            @Size(max = 15, message = "PRODUCT_NAME_TOO_LONG") String name,
            @NotNull(message = "PRICE_MISSING") Long price) {
    }

    record FallbackRequest(@NotNull String name) {

    }

    @RestController
    static class TestController {

        @PostMapping("/test/validation")
        void validation(@Valid @RequestBody TestRequest request) {
        }

        @PostMapping("/test/fallback")
        void fallback(@Valid @RequestBody FallbackRequest request) {
        }

        @GetMapping("/test/business")
        void business() {
            throw new BusinessException(ErrorCode.NOT_EXIST_PRODUCT);
        }

        @GetMapping("/test/items/{id}")
        void item(@PathVariable Long id) {
        }

        @GetMapping("/test/unexpected")
        void unexpected() {
            throw new IllegalStateException("테스트용 예외");
        }
    }
}