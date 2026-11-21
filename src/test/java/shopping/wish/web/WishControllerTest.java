package shopping.wish.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import shopping.member.application.MemberService;
import shopping.member.domain.Email;
import shopping.member.persistence.MemberRepository;
import shopping.product.domain.Product;
import shopping.product.persistence.ProductRepository;
import shopping.wish.domain.Wish;
import shopping.wish.persistence.WishRepository;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("위시리스트 컨트롤러")
class WishControllerTest {

    @Autowired
    private MockMvc mvc;
    @Autowired
    private JdbcTemplate jdbc;
    @Autowired
    private WishRepository wishes;
    @Autowired
    private ProductRepository products;
    @Autowired
    private MemberService registration;
    @Autowired
    private MemberRepository members;
    private String owner;
    private String other;
    private Long ownerId;
    private Long otherId;
    private Product product;

    @BeforeEach
    void prepare() {
        clearDatabase();
        owner = register("wish-owner@example.com");
        other = register("wish-other@example.com");
        ownerId = memberId("wish-owner@example.com");
        otherId = memberId("wish-other@example.com");
        product = products.saveAndFlush(new Product("상품", 1000, "https://example.com/a"));
    }

    @DisplayName("인증된 회원의 위시에 상품을 추가한다")
    @Test
    void addsProductForAuthenticatedMember() throws Exception {
        add(owner).andExpect(status().isCreated()).andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.product.id").value(product.id()));

        assertThat(wishes.existsByMemberIdAndProduct_Id(ownerId, product.id())).isTrue();
        assertThat(wishes.existsByMemberIdAndProduct_Id(otherId, product.id())).isFalse();
    }

    @DisplayName("위시 중복 추가 여부는 인증된 회원을 기준으로 판단한다")
    @Test
    void duplicateCheckIsScopedToAuthenticatedMember() throws Exception {
        add(owner).andExpect(status().isCreated());

        add(owner).andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("WISH_ALREADY_EXISTS"));
        add(other).andExpect(status().isCreated());
    }

    @DisplayName("존재하지 않는 상품을 요청하면 상품 없음 오류를 반환한다")
    @Test
    void missingProductReturnsNotFound() throws Exception {
        request(owner, "{\"productId\":999999}").andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("PRODUCT_NOT_FOUND"));
    }

    @DisplayName("위시 추가 시 상품 ID 누락이나 회원 직접 지정은 거절한다")
    @ParameterizedTest
    @ValueSource(strings = {"{}", "{\"productId\":1,\"memberId\":999}"})
    void rejectsMissingProductIdOrClientSuppliedMember(String body) throws Exception {
        request(owner, body).andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_REQUEST"));
    }

    @DisplayName("위시 추가 시 쿼리로 회원을 지정하면 거절하고 저장하지 않는다")
    @Test
    void rejectsMemberSelectionWhenAddingWish() throws Exception {
        mvc.perform(post("/api/wishes").queryParam("memberId", otherId.toString())
                .header("Authorization", owner).contentType("application/json")
                .content("{\"productId\":" + product.id() + "}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_REQUEST"));

        assertThat(wishes.count()).isZero();
    }

    @DisplayName("위시 추가에는 유효한 인증 토큰이 필요하다")
    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"Basic invalid", "Bearer unknown"})
    void requiresValidToken(String authorization) throws Exception {
        request(authorization, "{\"productId\":" + product.id() + "}")
                .andExpect(status().isUnauthorized())
                .andExpect(header().string("WWW-Authenticate", "Bearer"))
                .andExpect(jsonPath("$.code").value("INVALID_TOKEN"));

        assertThat(wishes.count()).isZero();
    }

    @DisplayName("만료된 토큰으로 위시에 상품을 추가할 수 없다")
    @Test
    void expiredTokenCannotAddWish() throws Exception {
        jdbc.update("update access_tokens set expires_at = "
                + "timestamp with time zone '2000-01-01 00:00:00Z'");

        add(owner).andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("INVALID_TOKEN"));
        assertThat(wishes.count()).isZero();
    }

    @DisplayName("본인의 위시만 ID 오름차순으로 반환한다")
    @Test
    void returnsOnlyOwnWishesInIdOrder() throws Exception {
        Wish first = wishes.saveAndFlush(new Wish(ownerId, product));
        wishes.saveAndFlush(new Wish(otherId, product));
        Product next = products.saveAndFlush(
                new Product("다음상품", 2000, "https://example.com/b"));
        Wish second = wishes.saveAndFlush(new Wish(ownerId, next));

        mvc.perform(get("/api/wishes").header("Authorization", owner)).andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(first.id()))
                .andExpect(jsonPath("$[1].id").value(second.id()));
    }

    @DisplayName("다른 회원의 위시만 있으면 빈 배열을 반환한다")
    @Test
    void returnsEmptyArrayWhenOnlyOtherMemberHasWishes() throws Exception {
        wishes.saveAndFlush(new Wish(otherId, product));

        mvc.perform(get("/api/wishes").header("Authorization", owner)).andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @DisplayName("위시 조회 시 상품의 현재 정보를 반환한다")
    @Test
    void returnsCurrentProductDetails() throws Exception {
        wishes.saveAndFlush(new Wish(ownerId, product));
        product.changeTo("현재상품", 2000, "https://example.com/new", text -> false);
        products.saveAndFlush(product);

        mvc.perform(get("/api/wishes").header("Authorization", owner)).andExpect(status().isOk())
                .andExpect(jsonPath("$[0].product.name").value("현재상품"))
                .andExpect(jsonPath("$[0].product.price").value(2000))
                .andExpect(jsonPath("$[0].product.imageUrl").value("https://example.com/new"));
    }

    @DisplayName("위시 조회 시 쿼리 파라미터로 회원을 지정할 수 없다")
    @Test
    void rejectsMemberSelectionByQueryParameter() throws Exception {
        mvc.perform(get("/api/wishes").param("memberId", otherId.toString())
                .header("Authorization", owner)).andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_REQUEST"));
    }

    @DisplayName("위시 조회에는 토큰 인증이 필요하다")
    @Test
    void requiresAuthentication() throws Exception {
        mvc.perform(get("/api/wishes")).andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("INVALID_TOKEN"));
    }

    @DisplayName("본인의 위시 중 요청한 항목만 삭제한다")
    @Test
    void deletesOnlyRequestedOwnWish() throws Exception {
        Wish target = wishes.saveAndFlush(new Wish(ownerId, product));
        Wish others = wishes.saveAndFlush(new Wish(otherId, product));
        Product next = products.saveAndFlush(
                new Product("다음상품", 2000, "https://example.com/b"));
        Wish retained = wishes.saveAndFlush(new Wish(ownerId, next));

        mvc.perform(delete("/api/wishes/{id}", target.id()).header("Authorization", owner))
                .andExpect(status().isNoContent()).andExpect(content().string(""));

        assertThat(wishes.findAll()).extracting(Wish::id)
                .containsExactlyInAnyOrder(others.id(), retained.id());
    }

    @DisplayName("다른 회원의 위시 삭제 요청은 권한 오류로 응답한다")
    @Test
    void cannotDeleteAnotherMembersWish() throws Exception {
        Wish target = wishes.saveAndFlush(new Wish(ownerId, product));

        mvc.perform(delete("/api/wishes/{id}", target.id()).header("Authorization", other))
                .andExpect(status().isForbidden()).andExpect(jsonPath("$.code").value("FORBIDDEN"));

        assertThat(wishes.existsById(target.id())).isTrue();
    }

    @DisplayName("존재하지 않는 위시 삭제 요청은 위시 없음 오류로 응답한다")
    @Test
    void missingWishReturnsNotFound() throws Exception {
        mvc.perform(delete("/api/wishes/999999").header("Authorization", owner))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("WISH_NOT_FOUND"));
    }

    @DisplayName("인증되지 않은 위시 삭제 요청은 거절하고 항목을 유지한다")
    @Test
    void requiresAuthenticationBeforeDeleting() throws Exception {
        Wish target = wishes.saveAndFlush(new Wish(ownerId, product));

        mvc.perform(delete("/api/wishes/{id}", target.id())).andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("INVALID_TOKEN"));

        assertThat(wishes.existsById(target.id())).isTrue();
    }

    private ResultActions add(String authorization) throws Exception {
        return request(authorization, "{\"productId\":" + product.id() + "}");
    }

    private ResultActions request(String authorization, String body) throws Exception {
        var request = post("/api/wishes").contentType("application/json").content(body);
        if (authorization != null) {
            request.header("Authorization", authorization);
        }
        return mvc.perform(request);
    }

    @AfterEach
    void clearDatabase() {
        jdbc.update("delete from wishes");
        jdbc.update("delete from access_tokens");
        jdbc.update("delete from members");
        jdbc.update("delete from products");
    }

    private String register(String email) {
        return "Bearer " + registration.register(email, "hello123").value();
    }

    private Long memberId(String email) {
        return members.findByEmail(new Email(email)).orElseThrow().id();
    }
}
