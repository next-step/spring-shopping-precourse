package shopping.product;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import shopping.AcceptanceTest;
import shopping.FakeProfanityCheckerConfig;

class ProductApiTest extends AcceptanceTest {
    @Test
    void 상품을_생성한다() throws Exception {
        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(productJson("Coffee", 4500)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.name").value("Coffee"))
                .andExpect(jsonPath("$.price").value(4500));
    }

    @Test
    void 유효하지_않은_상품은_400을_반환한다() throws Exception {
        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(productJson("Coffee!", 4500)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void 비속어가_포함된_상품은_400을_반환한다() throws Exception {
        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(productJson(FakeProfanityCheckerConfig.PROFANITY, 4500)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void 상품을_조회한다() throws Exception {
        Long productId = createProduct("Coffee");

        mockMvc.perform(get("/api/products/" + productId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(productId));
    }

    @Test
    void 상품_목록을_조회한다() throws Exception {
        createProduct("Coffee");
        createProduct("Tea");

        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void 없는_상품을_조회하면_404를_반환한다() throws Exception {
        mockMvc.perform(get("/api/products/" + Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    void 상품을_수정한다() throws Exception {
        Long productId = createProduct("Coffee");

        mockMvc.perform(put("/api/products/" + productId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(productJson("Latte", 5000)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Latte"))
                .andExpect(jsonPath("$.price").value(5000));
    }

    @Test
    void 상품을_삭제한다() throws Exception {
        Long productId = createProduct("Coffee");

        mockMvc.perform(delete("/api/products/" + productId))
                .andExpect(status().isNoContent());
        mockMvc.perform(get("/api/products/" + productId))
                .andExpect(status().isNotFound());
    }
}
