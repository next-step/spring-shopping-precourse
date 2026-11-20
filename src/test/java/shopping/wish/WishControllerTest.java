package shopping.wish;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import shopping.auth.Authenticator;
import shopping.auth.JwtTokenProvider;
import shopping.common.GlobalExceptionHandler;
import shopping.member.Member;
import shopping.member.MemberRepository;
import shopping.product.Product;
import shopping.product.ProductName;
import shopping.product.ProductRepository;

class WishControllerTest {

    private static final String EMAIL = "hong@example.com";

    private MockMvc mockMvc;
    private String token;
    private Long productId;

    @BeforeEach
    void setUp() {
        JwtTokenProvider provider =
                new JwtTokenProvider("test-secret-key-for-jwt-test-1234567890", 3_600_000);
        MemberRepository memberRepository = new MemberRepository();
        memberRepository.save(new Member(null, EMAIL, "1234"));
        ProductRepository productRepository = new ProductRepository();
        productId = productRepository.save(new Product(
                null, new ProductName("apple"), 1000, "https://example.com/a.png")).getId();
        WishService service = new WishService(new WishRepository(), productRepository);
        token = provider.createToken(EMAIL);
        mockMvc = MockMvcBuilders
                .standaloneSetup(new WishController(service, new Authenticator(provider, memberRepository)))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    private ResultActions addWish(Long targetProductId) throws Exception {
        return mockMvc.perform(post("/api/wishes")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"productId\":" + targetProductId + "}"));
    }

    private String listWishes() throws Exception {
        return mockMvc.perform(get("/api/wishes").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
    }

    @Test
    void 위시리스트에_추가하면_201이다() throws Exception {
        addWish(productId).andExpect(status().isCreated());
    }

    @Test
    void 토큰_없이_추가하면_401이다() throws Exception {
        mockMvc.perform(post("/api/wishes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"productId\":1}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void 없는_상품을_추가하면_404이다() throws Exception {
        addWish(999L).andExpect(status().isNotFound());
    }

    @Test
    void 같은_상품을_두_번_추가하면_409이다() throws Exception {
        addWish(productId);

        addWish(productId).andExpect(status().isConflict());
    }

    @Test
    void 위시리스트를_조회하면_담은_상품이_포함된다() throws Exception {
        addWish(productId);

        assertThat(listWishes()).contains("apple");
    }

    @Test
    void 토큰_없이_조회하면_401이다() throws Exception {
        mockMvc.perform(get("/api/wishes")).andExpect(status().isUnauthorized());
    }

    @Test
    void 위시를_삭제하면_204이고_목록에서_사라진다() throws Exception {
        addWish(productId);

        mockMvc.perform(delete("/api/wishes/1").header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());
        assertThat(listWishes()).isEqualTo("[]");
    }

    @Test
    void 없는_위시를_삭제하면_404이다() throws Exception {
        mockMvc.perform(delete("/api/wishes/999").header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());
    }

    @Test
    void 토큰_없이_삭제하면_401이다() throws Exception {
        mockMvc.perform(delete("/api/wishes/1")).andExpect(status().isUnauthorized());
    }
}