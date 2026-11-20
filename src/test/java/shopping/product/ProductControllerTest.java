package shopping.product;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import shopping.common.GlobalExceptionHandler;

class ProductControllerTest {

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        ProductService service = new ProductService(
                new ProductRepository(), new ProductNameFactory(text -> false));
        mockMvc = MockMvcBuilders.standaloneSetup(new ProductController(service))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    private String body(String name) {
        return "{\"name\":\"" + name
                + "\",\"price\":1000,\"imageUrl\":\"https://example.com/a.png\"}";
    }

    private void createProduct(String name) throws Exception {
        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body(name)));
    }

    @Test
    void 상품을_생성하면_201과_상품_정보를_반환한다() throws Exception {
        String response = mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body("사과")))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);

        assertThat(response).contains("\"name\":\"사과\"");
    }

    @Test
    void 허용되지_않은_이름으로_생성하면_400이다() throws Exception {
        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body("사과@")))
                .andExpect(status().isBadRequest());
    }

    @Test
    void JSON_형식이_잘못되면_400이다() throws Exception {
        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("not json"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void 상품을_조회하면_200이다() throws Exception {
        createProduct("사과");

        mockMvc.perform(get("/api/products/1")).andExpect(status().isOk());
    }

    @Test
    void 없는_상품을_조회하면_404이다() throws Exception {
        mockMvc.perform(get("/api/products/999")).andExpect(status().isNotFound());
    }

    @Test
    void 상품_목록을_조회하면_모든_상품이_포함된다() throws Exception {
        createProduct("사과");
        createProduct("배");

        String response = mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);

        assertThat(response).contains("사과", "배");
    }

    @Test
    void 상품을_수정하면_200이다() throws Exception {
        createProduct("사과");

        mockMvc.perform(put("/api/products/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body("배")))
                .andExpect(status().isOk());
    }

    @Test
    void 없는_상품을_수정하면_404이다() throws Exception {
        mockMvc.perform(put("/api/products/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body("배")))
                .andExpect(status().isNotFound());
    }

    @Test
    void 상품을_삭제하면_204이고_이후_조회되지_않는다() throws Exception {
        createProduct("사과");

        mockMvc.perform(delete("/api/products/1")).andExpect(status().isNoContent());
        mockMvc.perform(get("/api/products/1")).andExpect(status().isNotFound());
    }

    @Test
    void 없는_상품을_삭제하면_404이다() throws Exception {
        mockMvc.perform(delete("/api/products/999")).andExpect(status().isNotFound());
    }
}