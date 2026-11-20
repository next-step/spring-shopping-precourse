package shopping.product;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.assertj.MvcTestResult;
import shopping.support.ApiTest;

class ProductApiTest extends ApiTest {

    @Test
    void 상품을_생성한다() {
        MvcTestResult result = post("/api/products", productJson("콜라", 1500, IMAGE_URL));

        assertThat(result).hasStatus(HttpStatus.CREATED);
        assertThat(result).bodyJson().extractingPath("$.name").isEqualTo("콜라");
        assertThat(result).bodyJson().extractingPath("$.price").isEqualTo(1500);
        assertThat(result).bodyJson().extractingPath("$.imageUrl").isEqualTo(IMAGE_URL);
        assertThat(result.getResponse().getHeader("Location"))
                .isEqualTo("/api/products/" + readId(result));
    }

    @Test
    void 특정_상품을_조회한다() {
        Long id = createProduct("콜라");

        MvcTestResult result = mvc.get().uri("/api/products/{id}", id).exchange();

        assertThat(result).hasStatusOk();
        assertThat(result).bodyJson().extractingPath("$.name").isEqualTo("콜라");
    }

    @Test
    void 모든_상품의_목록을_조회한다() {
        createProduct("콜라");
        createProduct("사이다");

        MvcTestResult result = mvc.get().uri("/api/products").exchange();

        assertThat(result).hasStatusOk();
        assertThat(result).bodyJson().extractingPath("$.length()").isEqualTo(2);
        assertThat(result).bodyJson().extractingPath("$[0].name").isEqualTo("콜라");
        assertThat(result).bodyJson().extractingPath("$[1].name").isEqualTo("사이다");
    }

    @Test
    void 상품을_수정한다() {
        Long id = createProduct("콜라");

        MvcTestResult result = put(id, productJson("사이다", 2000, IMAGE_URL));

        assertThat(result).hasStatusOk();
        assertThat(result).bodyJson().extractingPath("$.name").isEqualTo("사이다");
        assertThat(result).bodyJson().extractingPath("$.price").isEqualTo(2000);
    }

    @Test
    void 상품을_삭제하면_더_이상_조회되지_않는다() {
        Long id = createProduct("콜라");

        MvcTestResult result = mvc.delete().uri("/api/products/{id}", id).exchange();

        assertThat(result).hasStatus(HttpStatus.NO_CONTENT);
        assertThat(mvc.get().uri("/api/products/{id}", id)).hasStatus(HttpStatus.NOT_FOUND);
    }

    @Test
    void 존재하지_않는_상품을_조회하면_404를_응답한다() {
        MvcTestResult result = mvc.get().uri("/api/products/{id}", 999).exchange();

        assertThat(result).hasStatus(HttpStatus.NOT_FOUND);
        assertThat(result).bodyJson().extractingPath("$.message").asString().contains("찾을 수 없습니다");
    }

    @Test
    void 존재하지_않는_상품을_수정하거나_삭제하면_404를_응답한다() {
        assertThat(put(999L, productJson("콜라", 1000, IMAGE_URL))).hasStatus(HttpStatus.NOT_FOUND);
        assertThat(mvc.delete().uri("/api/products/{id}", 999)).hasStatus(HttpStatus.NOT_FOUND);
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "열여섯글자가넘는아주아주긴상품이름", "콜라!"})
    void 이름이_규칙에_맞지_않으면_400을_응답한다(String name) {
        MvcTestResult result = post("/api/products", productJson(name, 1000, IMAGE_URL));

        assertThat(result).hasStatus(HttpStatus.BAD_REQUEST);
        assertThat(result).bodyJson().extractingPath("$.message").asString().isNotBlank();
    }

    @Test
    void 가격이_음수이면_400을_응답한다() {
        MvcTestResult result = post("/api/products", productJson("콜라", -1, IMAGE_URL));

        assertThat(result).hasStatus(HttpStatus.BAD_REQUEST);
    }

    @Test
    void 가격이_없으면_400을_응답한다() {
        String json = """
                {"name": "콜라", "imageUrl": "%s"}
                """.formatted(IMAGE_URL);

        MvcTestResult result = post("/api/products", json);

        assertThat(result).hasStatus(HttpStatus.BAD_REQUEST);
        assertThat(result).bodyJson().extractingPath("$.message").isEqualTo("상품 가격은 필수입니다.");
    }

    @Test
    void 가격이_숫자가_아니면_400을_응답한다() {
        String json = """
                {"name": "콜라", "price": "abc", "imageUrl": "%s"}
                """.formatted(IMAGE_URL);

        MvcTestResult result = post("/api/products", json);

        assertThat(result).hasStatus(HttpStatus.BAD_REQUEST);
        assertThat(result).bodyJson().extractingPath("$.message").isEqualTo("요청 본문을 읽을 수 없습니다.");
    }

    @Test
    void 이미지_URL이_올바르지_않으면_400을_응답한다() {
        MvcTestResult result = post("/api/products", productJson("콜라", 1000, "not-a-url"));

        assertThat(result).hasStatus(HttpStatus.BAD_REQUEST);
    }

    @Test
    void 요청_본문이_JSON이_아니면_400을_응답한다() {
        MvcTestResult result = post("/api/products", "이건 JSON이 아니다");

        assertThat(result).hasStatus(HttpStatus.BAD_REQUEST);
    }

    @Test
    void 잘못된_값으로_수정하면_400을_응답하고_기존_값이_유지된다() {
        Long id = createProduct("콜라");

        MvcTestResult result = put(id, productJson("사이다!", 1, IMAGE_URL));

        assertThat(result).hasStatus(HttpStatus.BAD_REQUEST);
        assertThat(mvc.get().uri("/api/products/{id}", id))
                .bodyJson().extractingPath("$.name").isEqualTo("콜라");
    }

    private MvcTestResult put(Long id, String json) {
        return mvc.put().uri("/api/products/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .exchange();
    }
}
