package shopping.wish;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.jayway.jsonpath.JsonPath;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import shopping.AcceptanceTest;

class WishApiTest extends AcceptanceTest {
    @Test
    void 위시_리스트에_상품을_추가한다() throws Exception {
        String token = register("user@example.com");
        Long productId = createProduct("Coffee");

        addWish(token, productId)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.productId").value(productId))
                .andExpect(jsonPath("$.name").value("Coffee"));
    }

    @Test
    void 토큰이_없으면_401을_반환한다() throws Exception {
        mockMvc.perform(get("/api/wishes"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void 유효하지_않은_토큰이면_401을_반환한다() throws Exception {
        mockMvc.perform(get("/api/wishes").header(HttpHeaders.AUTHORIZATION, bearer("invalid")))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void 같은_상품을_두_번_추가하면_409를_반환한다() throws Exception {
        String token = register("user@example.com");
        Long productId = createProduct("Coffee");
        addWish(token, productId);

        addWish(token, productId).andExpect(status().isConflict());
    }

    @Test
    void 없는_상품을_추가하면_404를_반환한다() throws Exception {
        String token = register("user@example.com");

        addWish(token, Long.MAX_VALUE).andExpect(status().isNotFound());
    }

    @Test
    void 내_위시_리스트만_조회한다() throws Exception {
        String token = register("user@example.com");
        String otherToken = register("other@example.com");
        addWish(token, createProduct("Coffee"));
        addWish(otherToken, createProduct("Tea"));

        mockMvc.perform(get("/api/wishes").header(HttpHeaders.AUTHORIZATION, bearer(token)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("Coffee"));
    }

    @Test
    void 위시_리스트에서_상품을_삭제한다() throws Exception {
        String token = register("user@example.com");
        Long wishId = wishIdOf(addWish(token, createProduct("Coffee")));

        mockMvc.perform(delete("/api/wishes/" + wishId).header(HttpHeaders.AUTHORIZATION, bearer(token)))
                .andExpect(status().isNoContent());
    }

    @Test
    void 다른_회원의_위시를_삭제하면_403을_반환한다() throws Exception {
        String token = register("user@example.com");
        String otherToken = register("other@example.com");
        Long wishId = wishIdOf(addWish(token, createProduct("Coffee")));

        mockMvc.perform(delete("/api/wishes/" + wishId).header(HttpHeaders.AUTHORIZATION, bearer(otherToken)))
                .andExpect(status().isForbidden());
    }

    private ResultActions addWish(String token, Long productId) throws Exception {
        return mockMvc.perform(post("/api/wishes")
                .header(HttpHeaders.AUTHORIZATION, bearer(token))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"productId\":" + productId + "}"));
    }

    private Long wishIdOf(ResultActions result) throws Exception {
        String body = result.andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        Number id = JsonPath.read(body, "$.id");
        return id.longValue();
    }
}
