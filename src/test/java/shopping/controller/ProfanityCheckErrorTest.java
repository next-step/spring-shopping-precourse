package shopping.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import shopping.domain.ProfanityCheckException;
import shopping.domain.ProfanityCheckException.Reason;
import shopping.domain.Products;
import shopping.service.ProductService;

class ProfanityCheckErrorTest {
    private static MockMvc mvcFailingWith(Reason reason) {
        ProductService service = new ProductService(new Products(), text -> {
            throw new ProfanityCheckException(reason, "내부 상세 원인: https://internal/secret");
        });
        return MockMvcBuilders.standaloneSetup(new ProductController(service))
                .setControllerAdvice(new ApiExceptionHandler())
                .build();
    }

    @ParameterizedTest
    @CsvSource(delimiter = '|', value = {
            "BAD_REQUEST|500|비속어 검사 요청에 문제가 있어 처리할 수 없습니다.",
            "SERVICE_ERROR|503|비속어 검사 서비스를 일시적으로 사용할 수 없습니다. 잠시 후 다시 시도해 주세요.",
            "CONNECTION_ERROR|503|비속어 검사 서비스에 연결할 수 없습니다. 잠시 후 다시 시도해 주세요.",
            "INVALID_RESPONSE|502|비속어 검사 서비스에서 올바르지 않은 응답을 받았습니다."})
    @DisplayName("비속어 검사에 실패하면 사유에 맞는 상태 코드와 메시지를 돌려주고 내부 정보는 숨긴다")
    void profanityCheckFailure(Reason reason, int status, String message) throws Exception {
        mvcFailingWith(reason)
                .perform(post("/api/products").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Item\",\"price\":1,\"imageUrl\":\"https://a.com\"}"))
                .andExpect(status().is(status))
                .andExpect(jsonPath("$.message").value(message));
    }
}
