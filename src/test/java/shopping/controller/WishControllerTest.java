package shopping.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import shopping.domain.Members;
import shopping.domain.Products;
import shopping.service.AuthService;
import shopping.service.MemberService;
import shopping.service.ProductService;
import shopping.service.TokenStore;
import shopping.service.WishService;

class WishControllerTest {
    private static final String URL = "https://example.com/a.png";

    private MockMvc mvc;
    private ProductService productService;
    private String token;
    private String otherToken;

    @BeforeEach
    void setUp() {
        Members members = new Members();
        TokenStore tokenStore = new TokenStore();
        MemberService memberService = new MemberService(members, tokenStore);
        productService = new ProductService(new Products(), text -> false);
        mvc = MockMvcBuilders.standaloneSetup(new WishController(new WishService(productService)))
                .setCustomArgumentResolvers(
                        new LoginMemberArgumentResolver(new AuthService(members, tokenStore)))
                .setControllerAdvice(new ApiExceptionHandler())
                .build();
        token = memberService.register("user@gmail.com", "abcd123!");
        otherToken = memberService.register("other@gmail.com", "abcd123!");
        productService.create("First", 1000, URL);
        productService.create("Second", 2000, URL);
    }

    private static MockHttpServletRequestBuilder withToken(MockHttpServletRequestBuilder builder,
            String bearer) {
        return builder.header("Authorization", "Bearer " + bearer);
    }

    private ResultActions add(String bearer, String json) throws Exception {
        return mvc.perform(withToken(post("/api/wishes"), bearer)
                .contentType(MediaType.APPLICATION_JSON).content(json));
    }

    private ResultActions add(String bearer, long productId) throws Exception {
        return add(bearer, "{\"productId\":" + productId + "}");
    }

    @Test
    @DisplayName("위시 리스트에 상품을 추가하면 201과 위시 항목을 돌려준다")
    void addWish() throws Exception {
        add(token, 1)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.wishId").value(1))
                .andExpect(jsonPath("$.product.id").value(1))
                .andExpect(jsonPath("$.product.name").value("First"))
                .andExpect(jsonPath("$.product.price").value(1000))
                .andExpect(jsonPath("$.product.imageUrl").value(URL));
    }

    @Test
    @DisplayName("존재하지 않는 상품을 추가하면 404를 돌려준다")
    void addUnknownProduct() throws Exception {
        add(token, 99)
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").isNotEmpty());
    }

    @Test
    @DisplayName("같은 상품을 두 번 추가하면 400을 돌려준다")
    void addDuplicate() throws Exception {
        add(token, 1).andExpect(status().isCreated());

        add(token, 1)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").isNotEmpty());
    }

    @ParameterizedTest
    @ValueSource(strings = {"{}", "{\"productId\":null}", "{\"productId\":0}",
            "{\"productId\":-1}", "{\"productId\":\"abc\"}", "{\"productId\":1.5}",
            "", "not json", "[]"})
    @DisplayName("상품 ID가 없거나 올바르지 않거나 JSON이 깨졌으면 400을 돌려준다")
    void addInvalidBody(String json) throws Exception {
        add(token, json)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").isNotEmpty());
    }

    @Test
    @DisplayName("위시 리스트를 담은 순서대로 조회하고 처음에는 빈 배열이다")
    void listWishes() throws Exception {
        mvc.perform(withToken(get("/api/wishes"), token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));

        add(token, 2);
        add(token, 1);

        mvc.perform(withToken(get("/api/wishes"), token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].wishId").value(2))
                .andExpect(jsonPath("$[0].product.name").value("Second"))
                .andExpect(jsonPath("$[1].wishId").value(1))
                .andExpect(jsonPath("$[1].product.name").value("First"));
    }

    @Test
    @DisplayName("위시 리스트는 본인 것만 조회된다")
    void listOnlyOwn() throws Exception {
        add(token, 1);
        add(otherToken, 2);

        mvc.perform(withToken(get("/api/wishes"), token))
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].product.name").value("First"));
        mvc.perform(withToken(get("/api/wishes"), otherToken))
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].product.name").value("Second"));
    }

    @Test
    @DisplayName("본인 위시를 삭제하면 204를 돌려주고 이후 조회되지 않는다")
    void deleteWish() throws Exception {
        add(token, 1);

        mvc.perform(withToken(delete("/api/wishes/1"), token)).andExpect(status().isNoContent());

        mvc.perform(withToken(get("/api/wishes"), token))
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    @DisplayName("담겨 있지 않은 위시를 삭제하면 404를 돌려준다")
    void deleteNotInWishList() throws Exception {
        mvc.perform(withToken(delete("/api/wishes/1"), token))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").isNotEmpty());
    }

    @Test
    @DisplayName("다른 회원의 위시는 삭제할 수 없고 그 회원의 목록은 유지된다")
    void deleteOthersWish() throws Exception {
        add(otherToken, 1);

        mvc.perform(withToken(delete("/api/wishes/1"), token)).andExpect(status().isNotFound());

        mvc.perform(withToken(get("/api/wishes"), otherToken))
                .andExpect(jsonPath("$.length()").value(1));
    }

    @ParameterizedTest
    @ValueSource(strings = {"0", "-1", "abc", "1.5"})
    @DisplayName("위시 ID가 올바르지 않으면 400을 돌려준다")
    void deleteInvalidId(String id) throws Exception {
        mvc.perform(withToken(delete("/api/wishes/" + id), token))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").isNotEmpty());
    }

    @Test
    @DisplayName("삭제한 위시는 다시 추가할 수 있다")
    void addAfterDelete() throws Exception {
        add(token, 1);
        mvc.perform(withToken(delete("/api/wishes/1"), token));

        add(token, 1).andExpect(status().isCreated());
    }

    @Test
    @DisplayName("상품이 삭제되면 위시 리스트에서 보이지 않는다")
    void deletedProductIsHidden() throws Exception {
        add(token, 1);
        add(token, 2);

        productService.delete(new shopping.domain.ProductId(1));

        mvc.perform(withToken(get("/api/wishes"), token))
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].product.name").value("Second"));
    }

    @Test
    @DisplayName("토큰이 없으면 모든 위시 API가 401을 돌려준다")
    void noToken() throws Exception {
        mvc.perform(post("/api/wishes").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"productId\":1}"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").isNotEmpty());
        mvc.perform(get("/api/wishes")).andExpect(status().isUnauthorized());
        mvc.perform(delete("/api/wishes/1")).andExpect(status().isUnauthorized());
    }

    @ParameterizedTest
    @ValueSource(strings = {"Bearer not-a-token", "Basic abc", "Bearer", "garbage", ""})
    @DisplayName("토큰이 올바르지 않으면 모든 위시 API가 401을 돌려준다")
    void invalidToken(String header) throws Exception {
        mvc.perform(post("/api/wishes").header("Authorization", header)
                        .contentType(MediaType.APPLICATION_JSON).content("{\"productId\":1}"))
                .andExpect(status().isUnauthorized());
        mvc.perform(get("/api/wishes").header("Authorization", header))
                .andExpect(status().isUnauthorized());
        mvc.perform(delete("/api/wishes/1").header("Authorization", header))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("인증 실패는 요청 본문 검사보다 먼저 처리된다")
    void authBeforeValidation() throws Exception {
        mvc.perform(post("/api/wishes").contentType(MediaType.APPLICATION_JSON)
                        .content("not json"))
                .andExpect(status().isUnauthorized());
    }
}
