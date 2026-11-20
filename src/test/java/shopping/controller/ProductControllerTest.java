package shopping.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import tools.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void 상품을_생성한다() throws Exception {
        mockMvc.perform(createProductRequest("상품", 1000, "http://image.png"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("상품"))
                .andExpect(jsonPath("$.price").value(1000))
                .andExpect(jsonPath("$.imageUrl").value("http://image.png"));
    }

    @Test
    void 상품_이름이_15자를_초과하면_400을_반환한다() throws Exception {
        mockMvc.perform(createProductRequest("일이삼사오육칠팔구십일이삼사오육", 1000, "http://image.png"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void 상품을_조회한다() throws Exception {
        Long productId = createProduct("조회상품", 2000, "http://image.png");

        mockMvc.perform(get("/api/products/" + productId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("조회상품"));
    }

    @Test
    void 존재하지_않는_상품을_조회하면_404를_반환한다() throws Exception {
        mockMvc.perform(get("/api/products/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void 상품_목록을_조회한다() throws Exception {
        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void 상품을_수정한다() throws Exception {
        Long productId = createProduct("수정전", 1000, "http://image.png");

        mockMvc.perform(put("/api/products/" + productId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(productJson("수정후", 2000, "http://new.png")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("수정후"))
                .andExpect(jsonPath("$.price").value(2000));
    }

    @Test
    void 상품을_삭제한다() throws Exception {
        Long productId = createProduct("삭제상품", 1000, "http://image.png");

        mockMvc.perform(delete("/api/products/" + productId))
                .andExpect(status().isOk());
    }

    private Long createProduct(String name, int price, String imageUrl) throws Exception {
        String response = mockMvc.perform(createProductRequest(name, price, imageUrl))
                .andReturn().getResponse().getContentAsString();

        return objectMapper.readTree(response).get("id").asLong();
    }

    private org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder createProductRequest(
            String name, int price, String imageUrl) {
        return post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(productJson(name, price, imageUrl));
    }

    private String productJson(String name, int price, String imageUrl) {
        return "{\"name\": \"%s\", \"price\": %d, \"imageUrl\": \"%s\"}".formatted(name, price, imageUrl);
    }
}
