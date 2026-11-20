package shopping.product;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ProductApiTest {

    private static final String STRAWBERRY =
            "{\"name\": \"딸기\", \"price\": 1000, \"imageUrl\": \"https://example.com/s.jpg\"}";
    private static final String TOO_LONG =
            "{\"name\": \"가나다라마바사아자차카타파하ab\", \"price\": 1000, "
                    + "\"imageUrl\": \"https://example.com/s.jpg\"}";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductRepository productRepository;

    @Test
    @DisplayName("상품을 등록하면 201")
    void createsProduct() throws Exception {
        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(STRAWBERRY))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("딸기"));
    }

    @Test
    @DisplayName("이름이 15자를 넘으면 400과 오류 메시지")
    void returns400WhenTooLong() throws Exception {
        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TOO_LONG))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("이름은 15글자를 초과할 수 없습니다."));
    }

    @Test
    @DisplayName("없는 상품을 조회하면 404")
    void returns404WhenNotFound() throws Exception {
        mockMvc.perform(get("/api/products/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("존재하지 않는 상품입니다."));
    }

    @Test
    @DisplayName("상품을 삭제하면 204")
    void deletesProduct() throws Exception {
        Product saved = productRepository.save(
                new Product("딸기", 1000, "https://example.com/s.jpg"));
        mockMvc.perform(delete("/api/products/" + saved.getId()))
                .andExpect(status().isNoContent());
    }
}