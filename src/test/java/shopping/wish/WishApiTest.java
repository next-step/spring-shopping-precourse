package shopping.wish;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;
import shopping.auth.JwtTokenProvider;
import shopping.member.Member;
import shopping.member.MemberRepository;
import shopping.product.Product;
import shopping.product.ProductRepository;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class WishApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private WishRepository wishRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    private Member memberA;
    private Product product;
    private String tokenA;
    private String tokenB;

    @BeforeEach
    void setUp() {
        memberA = memberRepository.save(new Member("a@test.com", "1234"));
        Member memberB = memberRepository.save(new Member("b@test.com", "1234"));
        product = productRepository.save(
                new Product("딸기", 1000, "https://example.com/strawberry.jpg"));
        tokenA = "Bearer " + jwtTokenProvider.createToken(memberA.getId());
        tokenB = "Bearer " + jwtTokenProvider.createToken(memberB.getId());
    }

    @Test
    @DisplayName("토큰 없이 위시를 담으면 401")
    void returns401WithoutToken() throws Exception {
        mockMvc.perform(post("/api/wishes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body()))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("로그인이 필요합니다."));
    }

    @Test
    @DisplayName("가짜 토큰으로 위시를 담으면 401")
    void returns401WithFakeToken() throws Exception {
        addWish("Bearer fake.token.value")
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("유효하지 않은 토큰입니다."));
    }

    @Test
    @DisplayName("로그인한 회원은 위시를 담을 수 있다 (201)")
    void addsWish() throws Exception {
        addWish(tokenA)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.product.name").value("딸기"));
    }

    @Test
    @DisplayName("같은 상품을 또 담으면 409")
    void returns409WhenDuplicate() throws Exception {
        addWish(tokenA);
        addWish(tokenA)
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("이미 위시에 담긴 상품입니다."));
    }

    @Test
    @DisplayName("내 위시 목록을 조회할 수 있다 (200)")
    void findsWishes() throws Exception {
        addWish(tokenA);
        mockMvc.perform(get("/api/wishes").header(HttpHeaders.AUTHORIZATION, tokenA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    @DisplayName("남의 위시를 삭제하면 403")
    void returns403WhenNotOwner() throws Exception {
        Wish wish = wishRepository.save(new Wish(memberA, product));
        mockMvc.perform(delete("/api/wishes/" + wish.getId())
                        .header(HttpHeaders.AUTHORIZATION, tokenB))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("본인의 위시만 삭제할 수 있습니다."));
    }

    @Test
    @DisplayName("내 위시는 삭제할 수 있다 (204)")
    void deletesWish() throws Exception {
        Wish wish = wishRepository.save(new Wish(memberA, product));
        mockMvc.perform(delete("/api/wishes/" + wish.getId())
                        .header(HttpHeaders.AUTHORIZATION, tokenA))
                .andExpect(status().isNoContent());
    }

    private ResultActions addWish(String token) throws Exception {
        return mockMvc.perform(post("/api/wishes")
                .header(HttpHeaders.AUTHORIZATION, token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body()));
    }

    private String body() {
        return "{\"productId\": " + product.getId() + "}";
    }
}