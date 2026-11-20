package shopping;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.jayway.jsonpath.JsonPath;
import java.time.Clock;
import java.time.Instant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest(
        properties = {
            "spring.datasource.url=jdbc:h2:mem:wish-api;DB_CLOSE_DELAY=-1",
            "spring.datasource.driver-class-name=org.h2.Driver",
            "spring.datasource.username=sa",
            "spring.datasource.password=",
            "spring.jpa.hibernate.ddl-auto=create-drop",
            "spring.flyway.enabled=false",
            "spring.jpa.open-in-view=false"
        })
@AutoConfigureMockMvc
@Transactional
class WishApiTest {

    @Autowired private MockMvc mockMvc;

    @Autowired private MemberRepository members;

    @Autowired private ProductRepository products;

    @Autowired private WishRepository wishes;

    @Autowired private TokenService tokenService;

    @MockitoBean private Clock clock;

    private final Instant now = Instant.parse("2026-11-21T00:00:00Z");
    private Member owner;
    private Member other;
    private Product product;
    private String ownerToken;
    private String otherToken;

    @BeforeEach
    void setUp() {
        given(clock.instant()).willReturn(now);
        owner = members.save(new Member("owner@example.com", "hash"));
        other = members.save(new Member("other@example.com", "hash"));
        product = products.saveAndFlush(new Product("keyboard", 1000, "https://example.com/a.jpg"));
        ownerToken = tokenService.issue(owner).token();
        otherToken = tokenService.issue(other).token();
    }

    @Test
    void addsAndListsWishUsingMemberFromToken() throws Exception {
        Long wishId = addWish(ownerToken, product.getId());
        mockMvc.perform(get("/api/wishes").header("Authorization", "Bearer " + ownerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(wishId.intValue()))
                .andExpect(jsonPath("$[0].product.id").value(product.getId().intValue()))
                .andExpect(jsonPath("$[0].product.name").value("keyboard"))
                .andExpect(jsonPath("$[0].product.price").value(1000))
                .andExpect(jsonPath("$[0].product.imageUrl").value("https://example.com/a.jpg"))
                .andExpect(jsonPath("$[0].member").doesNotExist());
    }

    @Test
    void listIgnoresClientSuppliedMemberIdAndSeparatesMembers() throws Exception {
        Long ownerWish = addWish(ownerToken, product.getId());
        addWish(otherToken, product.getId());
        mockMvc.perform(
                        get("/api/wishes")
                                .param("memberId", other.getId().toString())
                                .header("Authorization", "Bearer " + ownerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(ownerWish.intValue()));
    }

    @Test
    void returnsEmptyArrayForMemberWithoutWishes() throws Exception {
        addWish(otherToken, product.getId());
        mockMvc.perform(get("/api/wishes").header("Authorization", "Bearer " + ownerToken))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    void duplicateReturnsConflictAndKeepsOneWish() throws Exception {
        addWish(ownerToken, product.getId());
        mockMvc.perform(
                        post("/api/wishes")
                                .header("Authorization", "Bearer " + ownerToken)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(productBody(product.getId())))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("DUPLICATE_WISH"));
        assertThat(wishes.count()).isEqualTo(1);
    }

    @Test
    void missingProductReturnsNotFoundWithoutSaving() throws Exception {
        mockMvc.perform(
                        post("/api/wishes")
                                .header("Authorization", "Bearer " + ownerToken)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(productBody(Long.MAX_VALUE)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("PRODUCT_NOT_FOUND"));
        assertThat(wishes.count()).isZero();
    }

    @Test
    void deletingOwnedWishReturnsEmpty204AndKeepsProduct() throws Exception {
        Long wishId = addWish(ownerToken, product.getId());
        mockMvc.perform(
                        delete("/api/wishes/" + wishId)
                                .header("Authorization", "Bearer " + ownerToken))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));
        assertThat(wishes.findById(wishId)).isEmpty();
        assertThat(products.findById(product.getId())).isPresent();
    }

    @Test
    void deletingAnotherMembersWishReturnsForbiddenAndKeepsIt() throws Exception {
        Long wishId = addWish(ownerToken, product.getId());
        mockMvc.perform(
                        delete("/api/wishes/" + wishId)
                                .header("Authorization", "Bearer " + otherToken))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("WISH_ACCESS_DENIED"));
        assertThat(wishes.findById(wishId)).isPresent();
    }

    @Test
    void deletingMissingWishReturnsNotFound() throws Exception {
        mockMvc.perform(
                        delete("/api/wishes/" + Long.MAX_VALUE)
                                .header("Authorization", "Bearer " + ownerToken))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("WISH_NOT_FOUND"));
    }

    @ParameterizedTest
    @ValueSource(
            strings = {
                "{}",
                "{",
                "{\"productId\":null}",
                "{\"productId\":0}",
                "{\"productId\":-1}",
                "{\"productId\":\"abc\"}"
            })
    void invalidWishRequestReturns400WithoutSaving(String body) throws Exception {
        mockMvc.perform(
                        post("/api/wishes")
                                .header("Authorization", "Bearer " + ownerToken)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_REQUEST"));
        assertThat(wishes.count()).isZero();
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(
            strings = {
                "Bearer",
                "Bearer ",
                "Basic abc",
                "Bearer unknown-token",
                "Bearer abc def",
                "Bearer abc,def"
            })
    void allWishOperationsRejectMissingOrInvalidAuthorization(String authorization)
            throws Exception {
        mockMvc.perform(withAuth(get("/api/wishes"), authorization))
                .andExpect(status().isUnauthorized())
                .andExpect(header().string("WWW-Authenticate", "Bearer"))
                .andExpect(jsonPath("$.code").value("INVALID_TOKEN"));
        mockMvc.perform(
                        withAuth(post("/api/wishes"), authorization)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(productBody(product.getId())))
                .andExpect(status().isUnauthorized());
        mockMvc.perform(withAuth(delete("/api/wishes/1"), authorization))
                .andExpect(status().isUnauthorized());
        assertThat(wishes.count()).isZero();
    }

    @Test
    void authenticatesBeforeBodyValidation() throws Exception {
        mockMvc.perform(post("/api/wishes").contentType(MediaType.APPLICATION_JSON).content("{"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("INVALID_TOKEN"));
    }

    @Test
    void rejectsExpiredTokenAtBoundaryForEveryWishOperation() throws Exception {
        given(clock.instant()).willReturn(now.plusSeconds(86400));
        String authorization = "Bearer " + ownerToken;
        mockMvc.perform(withAuth(get("/api/wishes"), authorization))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("INVALID_TOKEN"));
        mockMvc.perform(
                        withAuth(post("/api/wishes"), authorization)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(productBody(product.getId())))
                .andExpect(status().isUnauthorized());
        mockMvc.perform(withAuth(delete("/api/wishes/1"), authorization))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void productDeletionRemovesWishesAcrossMembers() throws Exception {
        addWish(ownerToken, product.getId());
        addWish(otherToken, product.getId());
        mockMvc.perform(delete("/api/products/" + product.getId()))
                .andExpect(status().isNoContent());
        mockMvc.perform(withAuth(get("/api/wishes"), "Bearer " + ownerToken))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
        mockMvc.perform(withAuth(get("/api/wishes"), "Bearer " + otherToken))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    void invalidWishPathReturnsCommon400() throws Exception {
        mockMvc.perform(
                        delete("/api/wishes/not-a-number")
                                .header("Authorization", "Bearer " + ownerToken))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_REQUEST"));
    }

    @Test
    void tokenFromMemberApiCanAccessWishes() throws Exception {
        String token = registerMemberAndGetToken();
        Long wishId = addWish(token, product.getId());
        Member member = members.findByEmail("signup@example.com").orElseThrow();
        assertThat(wishes.findById(wishId).orElseThrow().isOwnedBy(member.getId())).isTrue();
    }

    private String registerMemberAndGetToken() throws Exception {
        String body = "{\"email\":\"signup@example.com\",\"password\":\"password123!\"}";
        String json =
                mockMvc.perform(
                                post("/api/members/register")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(body))
                        .andExpect(status().isCreated())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();
        return JsonPath.read(json, "$.token");
    }

    private Long addWish(String token, Long productId) throws Exception {
        MockHttpServletResponse response =
                mockMvc.perform(
                                post("/api/wishes")
                                        .header("Authorization", "Bearer " + token)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(productBody(productId)))
                        .andExpect(status().isCreated())
                        .andReturn()
                        .getResponse();
        Number wishId = JsonPath.read(response.getContentAsString(), "$.id");
        assertThat(response.getHeader("Location")).isEqualTo("/api/wishes/" + wishId);
        return wishId.longValue();
    }

    private MockHttpServletRequestBuilder withAuth(
            MockHttpServletRequestBuilder request, String authorization) {
        if (authorization != null) {
            request.header("Authorization", authorization);
        }
        return request;
    }

    private static String productBody(Long productId) {
        return "{\"productId\":" + productId + "}";
    }
}
