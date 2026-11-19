package shopping.product;

import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.mockito.BDDMockito.given;
import static org.hamcrest.Matchers.hasValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import shopping.common.ExternalApiException;
import shopping.product.domain.ProfanityChecker;

@SpringBootTest
@AutoConfigureMockMvc
class ProductApiTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProfanityChecker profanityChecker;

    @Test
    void 상품_이름에_비속어가_있으면_400을_반환한다() throws Exception {
        given(profanityChecker.containsProfanity("badword")).willReturn(true);

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(상품_JSON("badword", 1000)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("상품 이름에 비속어를 포함할 수 없습니다."));
    }

    @Test
    void 비속어_검사_API가_실패하면_500을_반환한다() throws Exception {
        given(profanityChecker.containsProfanity("사과"))
                .willThrow(new ExternalApiException("비속어 검사 서비스 호출에 실패했습니다.", null));

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(상품_JSON("사과", 1000)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void 상품을_등록하면_201과_등록된_상품을_반환한다() throws Exception {
        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name": "[특가] 사과", "price": 1000, "imageUrl": "https://example.com/apple.png"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("[특가] 사과"))
                .andExpect(jsonPath("$.price").value(1000));
    }

    @Test
    void 이름이_잘못되면_400을_반환한다() throws Exception {
        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name": "사과!", "price": 1000, "imageUrl": "https://example.com/apple.png"}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void 없는_상품을_조회하면_404를_반환한다() throws Exception {
        mockMvc.perform(get("/api/products/{productId}", 999_999))
                .andExpect(status().isNotFound());
    }

    @Test
    void 상품_목록을_조회한다() throws Exception {
        상품을_등록하면_201과_등록된_상품을_반환한다();
        상품을_등록하면_201과_등록된_상품을_반환한다();

        mockMvc.perform(get("/api/products"))
                .andExpect(jsonPath("$.length()", greaterThanOrEqualTo(2)));
    }

    @Test
    void 상품_가격을_수정한다() throws Exception {
        String location = 상품_등록("사과", 1000);

        mockMvc.perform(put(location)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(상품_JSON("사과", 2000)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.price").value(2000));

        mockMvc.perform(get(location))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.price").value(2000));
    }

    @Test
    void 상품을_삭제한다() throws Exception {
        String location = 상품_등록("사과", 1000);

        mockMvc.perform(delete(location))
                .andExpect(status().isNoContent());
    }

    private String 상품_등록(String name, int price) throws Exception {
        return mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(상품_JSON(name, price)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getHeader("Location");
    }

    private String 상품_JSON(String name, int price) {
        return """
                {"name": "%s", "price": %d, "imageUrl": "https://example.com/a.png"}
                """.formatted(name, price);
    }
}
