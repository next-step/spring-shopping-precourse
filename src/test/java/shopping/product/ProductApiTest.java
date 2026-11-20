package shopping.product;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import shopping.ApiTest;
import shopping.FakeProfanityCheckerConfig;

class ProductApiTest extends ApiTest {

    @Test
    void 상품을_생성하면_201과_생성된_상품을_반환한다() throws Exception {
        requestCreate(productJson("아이스 아메리카노", 4500))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.name").value("아이스 아메리카노"))
                .andExpect(jsonPath("$.price").value(4500))
                .andExpect(jsonPath("$.imageUrl").value(IMAGE_URL));
    }

    @Test
    void 상품_하나를_조회한다() throws Exception {
        long productId = createProduct("콜라(500ml)", 2000);

        mockMvc.perform(get("/api/products/{productId}", productId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(productId))
                .andExpect(jsonPath("$.name").value("콜라(500ml)"));
    }

    @Test
    void 없는_상품을_조회하면_404와_오류_메시지를_반환한다() throws Exception {
        mockMvc.perform(get("/api/products/{productId}", 999_999))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("상품을 찾을 수 없습니다."));
    }

    @Test
    void 상품_목록을_조회한다() throws Exception {
        createProduct("사과", 1000);
        createProduct("배", 2000);

        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void 상품을_수정한다() throws Exception {
        long productId = createProduct("사과", 1000);

        mockMvc.perform(put("/api/products/{productId}", productId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(productJson("[특가] 사과", 800)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("[특가] 사과"))
                .andExpect(jsonPath("$.price").value(800));
    }

    @Test
    void 없는_상품을_수정하면_404() throws Exception {
        mockMvc.perform(put("/api/products/{productId}", 999_999)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(productJson("사과", 1000)))
                .andExpect(status().isNotFound());
    }

    @Test
    void 상품을_삭제하면_204이고_다시_조회할_수_없다() throws Exception {
        long productId = createProduct("사과", 1000);

        mockMvc.perform(delete("/api/products/{productId}", productId))
                .andExpect(status().isNoContent());
        mockMvc.perform(get("/api/products/{productId}", productId))
                .andExpect(status().isNotFound());
    }

    @Test
    void 없는_상품을_삭제하면_404() throws Exception {
        mockMvc.perform(delete("/api/products/{productId}", 999_999))
                .andExpect(status().isNotFound());
    }

    @ParameterizedTest
    @ValueSource(strings = {"사과!", "가나다라마바사아자차카타파하가나", "   "})
    void 상품_이름이_규칙에_맞지_않으면_400과_오류_메시지를_반환한다(String name) throws Exception {
        requestCreate(productJson(name, 1000))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").isNotEmpty());
    }

    @Test
    void 상품_이름에_비속어가_있으면_400() throws Exception {
        requestCreate(productJson(FakeProfanityCheckerConfig.PROFANITY + " 사과", 1000))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("상품 이름에 비속어를 포함할 수 없습니다."));
    }

    @Test
    void 상품을_수정할_때도_비속어를_검사한다() throws Exception {
        long productId = createProduct("사과", 1000);

        mockMvc.perform(put("/api/products/{productId}", productId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(productJson(FakeProfanityCheckerConfig.PROFANITY, 1000)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void 가격이_없거나_음수면_400() throws Exception {
        requestCreate(productJson("사과", null))
                .andExpect(status().isBadRequest());
        requestCreate(productJson("사과", -1))
                .andExpect(status().isBadRequest());
    }

    @Test
    void 이미지_URL이_http로_시작하지_않으면_400() throws Exception {
        String json = """
                {"name": "사과", "price": 1000, "imageUrl": "ftp://example.com/a.png"}
                """;

        requestCreate(json)
                .andExpect(status().isBadRequest());
    }

    @Test
    void 요청_JSON_형식이_잘못되면_400과_오류_메시지를_반환한다() throws Exception {
        requestCreate("{\"name\": ")
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("요청 형식이 올바르지 않습니다."));
    }

    @Test
    void 상품_ID가_숫자가_아니면_400() throws Exception {
        mockMvc.perform(get("/api/products/{productId}", "abc"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("요청 형식이 올바르지 않습니다."));
    }

    private ResultActions requestCreate(String json) throws Exception {
        return mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json));
    }
}
