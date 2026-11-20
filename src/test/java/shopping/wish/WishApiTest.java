package shopping.wish;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.jayway.jsonpath.JsonPath;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import shopping.ApiTest;

class WishApiTest extends ApiTest {

    @PersistenceContext
    private EntityManager entityManager;

    private String token;
    private long productId;

    @BeforeEach
    void setUp() throws Exception {
        token = register("guk@example.com", "password1");
        productId = createProduct("사과", 1000);
    }

    @Test
    void 위시_리스트에_상품을_추가하면_201() throws Exception {
        requestAdd(token, productId)
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.productId").value(productId))
                .andExpect(jsonPath("$.name").value("사과"));
    }

    @Test
    void 토큰_없이_요청하면_401() throws Exception {
        mockMvc.perform(get("/api/wishes"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("인증 토큰이 필요합니다."));
    }

    @Test
    void 잘못된_토큰으로_요청하면_401() throws Exception {
        requestAdd("wrong-token", productId)
                .andExpect(status().isUnauthorized());
    }

    @Test
    void 없는_상품을_추가하면_404() throws Exception {
        requestAdd(token, 999_999L)
                .andExpect(status().isNotFound());
    }

    @Test
    void 같은_상품을_다시_추가하면_400() throws Exception {
        requestAdd(token, productId);

        requestAdd(token, productId)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("이미 위시 리스트에 담은 상품입니다."));
    }

    @Test
    void 내_위시_리스트만_조회한다() throws Exception {
        requestAdd(token, productId);
        String otherToken = register("other@example.com", "password1");
        requestAdd(otherToken, createProduct("배", 2000));

        mockMvc.perform(get("/api/wishes").header(HttpHeaders.AUTHORIZATION, bearer(token)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name").value("사과"));
    }

    @Test
    void 위시를_삭제하면_204이고_목록에서_사라진다() throws Exception {
        long wishId = addWish(token, productId);

        requestRemove(token, wishId)
                .andExpect(status().isNoContent());
        mockMvc.perform(get("/api/wishes").header(HttpHeaders.AUTHORIZATION, bearer(token)))
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void 없는_위시를_삭제하면_404() throws Exception {
        requestRemove(token, 999_999L)
                .andExpect(status().isNotFound());
    }

    @Test
    void 다른_회원의_위시를_삭제하면_403() throws Exception {
        long wishId = addWish(token, productId);
        String otherToken = register("other@example.com", "password1");

        requestRemove(otherToken, wishId)
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("다른 회원의 위시는 삭제할 수 없습니다."));
    }

    @Test
    void 상품을_삭제하면_위시_리스트에서도_사라진다() throws Exception {
        requestAdd(token, productId);
        startNewRequestContext();

        mockMvc.perform(delete("/api/products/{productId}", productId));

        mockMvc.perform(get("/api/wishes").header(HttpHeaders.AUTHORIZATION, bearer(token)))
                .andExpect(jsonPath("$", hasSize(0)));
    }

    private ResultActions requestAdd(String token, long productId) throws Exception {
        return mockMvc.perform(post("/api/wishes")
                .header(HttpHeaders.AUTHORIZATION, bearer(token))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"productId\": " + productId + "}"));
    }

    private ResultActions requestRemove(String token, long wishId) throws Exception {
        return mockMvc.perform(delete("/api/wishes/{wishId}", wishId)
                .header(HttpHeaders.AUTHORIZATION, bearer(token)));
    }

    private long addWish(String token, long productId) throws Exception {
        String content = requestAdd(token, productId).andReturn().getResponse().getContentAsString();
        return ((Number) JsonPath.read(content, "$.id")).longValue();
    }

    private void startNewRequestContext() {
        entityManager.flush();
        entityManager.clear();
    }

    private String bearer(String token) {
        return "Bearer " + token;
    }
}
