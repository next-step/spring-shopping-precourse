package shopping.controller;

import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.doThrow;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.test.web.servlet.assertj.MvcTestResult;
import org.springframework.web.server.ResponseStatusException;
import shopping.PurgoMalumClient;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.node.ObjectNode;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.springframework.http.MediaType.APPLICATION_JSON;

class ProductControllerTest {
    private final JsonMapper json = new JsonMapper();
    private final PurgoMalumClient filter = mock(PurgoMalumClient.class);
    private final MockMvcTester mvc = MockMvcTester.of(new ProductController(filter));

    @Test
    void 상품을_등록하면_목록과_상세에서_조회된다() {
        assertThat(mvc.get().uri("/api/products")).bodyJson().isStrictlyEqualTo("[]");
        long id = createProduct("머그컵");
        String expected = productJson(id, "머그컵", 12000, "mug");

        assertThat(mvc.get().uri("/api/products/{id}", id)).hasStatusOk()
                .bodyJson().isStrictlyEqualTo(expected);
        assertThat(mvc.get().uri("/api/products")).hasStatusOk()
                .bodyJson().isStrictlyEqualTo("[" + expected + "]");
    }

    @Test
    void 상품을_수정하면_같은_ID에_새_정보가_저장된다() {
        long id = createProduct("머그컵");
        String expected = productJson(id, "파란컵", 15000, "blue");

        assertThat(update(id, "파란컵", 15000, "blue")).hasStatusOk()
                .bodyJson().isStrictlyEqualTo(expected);
        assertThat(mvc.get().uri("/api/products/{id}", id)).bodyJson().isStrictlyEqualTo(expected);
    }

    @Test
    void 삭제한_상품은_조회하거나_수정하거나_다시_삭제할_수_없다() {
        long id = createProduct("머그컵");

        assertThat(mvc.delete().uri("/api/products/{id}", id)).hasStatus(HttpStatus.NO_CONTENT)
                .bodyText().isEmpty();
        assertThat(mvc.get().uri("/api/products/{id}", id)).hasStatus(HttpStatus.NOT_FOUND);
        assertThat(update(id, "파란컵", 15000, "blue")).hasStatus(HttpStatus.NOT_FOUND);
        assertThat(mvc.delete().uri("/api/products/{id}", id)).hasStatus(HttpStatus.NOT_FOUND);
    }

    @Test
    void 공백과_허용_기호를_포함한_15자_상품명을_등록하고_수정할_수_있다() {
        long id = createProduct("머그컵 ()[]+-&/_12");

        assertThat(update(id, "파란컵 ()[]+-&/_34", 15000, "blue")).hasStatusOk();
    }

    @ParameterizedTest
    @EmptySource
    @ValueSource(strings = {"   ", "머그컵!", "머그컵 ()[]+-&/_123"})
    void 잘못된_이름은_등록과_수정에서_거절하고_원래_상품을_유지한다(String name) {
        long id = createProduct("머그컵");

        assertThat(create(name)).hasStatus(HttpStatus.BAD_REQUEST);
        assertThat(update(id, name, 15000, "blue")).hasStatus(HttpStatus.BAD_REQUEST);
        assertThat(mvc.get().uri("/api/products")).bodyJson()
                .isStrictlyEqualTo("[" + productJson(id, "머그컵", 12000, "mug") + "]");
    }

    @Test
    void 비속어로_판정된_상품명은_등록과_수정에서_거절한다() {
        doThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST)).when(filter).validate("금칙어");
        long id = createProduct("머그컵");

        assertThat(create("금칙어")).hasStatus(HttpStatus.BAD_REQUEST);
        assertThat(update(id, "금칙어", 15000, "blue")).hasStatus(HttpStatus.BAD_REQUEST);
        assertThat(mvc.get().uri("/api/products")).bodyJson()
                .isStrictlyEqualTo("[" + productJson(id, "머그컵", 12000, "mug") + "]");
    }

    private long createProduct(String name) {
        MvcTestResult result = create(name);
        assertThat(result).hasStatus(HttpStatus.CREATED);
        byte[] body = result.getMvcResult().getResponse().getContentAsByteArray();
        long id = json.readTree(body).required("productId").longValue();
        assertThat(result).bodyJson().isStrictlyEqualTo(productJson(id, name, 12000, "mug"));
        return id;
    }

    private MvcTestResult create(String name) {
        return mvc.post().uri("/api/products").contentType(APPLICATION_JSON)
                .content(fields(name, 12000, "mug").toString()).exchange();
    }

    private MvcTestResult update(long id, String name, int price, String image) {
        return mvc.put().uri("/api/products/{id}", id).contentType(APPLICATION_JSON)
                .content(fields(name, price, image).toString()).exchange();
    }

    private ObjectNode fields(String name, int price, String image) {
        return json.createObjectNode()
                .put("productName", name)
                .put("price", price)
                .put("imageUrl", "https://example.com/" + image + ".png");
    }

    private String productJson(long id, String name, int price, String image) {
        return fields(name, price, image).put("productId", id).toString();
    }
}