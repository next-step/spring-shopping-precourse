package shopping.controller;

import org.junit.jupiter.api.BeforeEach;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class WishControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private String token;

    @BeforeEach
    void setUp() throws Exception {
        token = registerAndGetToken("wish" + System.nanoTime() + "@email.com", "1234");

        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\": \"위시상품\", \"price\": 1000, \"imageUrl\": \"http://image.png\"}"));
    }

    @Test
    void 위시_리스트에_상품을_추가한다() throws Exception {
        mockMvc.perform(addWishRequest(token, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productId").value(1));
    }

    @Test
    void 위시_리스트를_조회한다() throws Exception {
        mockMvc.perform(addWishRequest(token, 1L));

        mockMvc.perform(get("/api/wishes")
                        .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void 위시_리스트에서_상품을_삭제한다() throws Exception {
        Long wishId = addWishAndGetId(token, 1L);

        mockMvc.perform(delete("/api/wishes/" + wishId)
                        .header("Authorization", token))
                .andExpect(status().isOk());
    }

    @Test
    void 토큰_없이_위시_리스트를_조회하면_실패한다() throws Exception {
        mockMvc.perform(get("/api/wishes"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void 유효하지_않은_토큰이면_401을_반환한다() throws Exception {
        mockMvc.perform(get("/api/wishes")
                        .header("Authorization", "invalid-token"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").exists());
    }

    private String registerAndGetToken(String email, String password) throws Exception {
        String response = mockMvc.perform(post("/api/members/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\": \"%s\", \"password\": \"%s\"}".formatted(email, password)))
                .andReturn().getResponse().getContentAsString();

        return objectMapper.readTree(response).get("token").asText();
    }

    private Long addWishAndGetId(String token, Long productId) throws Exception {
        String response = mockMvc.perform(addWishRequest(token, productId))
                .andReturn().getResponse().getContentAsString();

        return objectMapper.readTree(response).get("id").asLong();
    }

    private org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder addWishRequest(
            String token, Long productId) {
        return post("/api/wishes")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"productId\": %d}".formatted(productId));
    }
}
