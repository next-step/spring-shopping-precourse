package shopping.config;

import static org.assertj.core.api.Assertions.assertThat;
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
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import shopping.domain.ProfanityChecker;

/** 실제 앱 설정(빈과 인자 해석기 등록)으로 요청이 처음부터 끝까지 동작하는지 확인한다. */
@SpringBootTest
@Import(ApiIntegrationTest.FakeProfanityConfig.class)
class ApiIntegrationTest {
    @TestConfiguration
    static class FakeProfanityConfig {
        @Bean
        @Primary
        ProfanityChecker fakeProfanityChecker() {
            return text -> text.contains("shit");
        }
    }

    @Autowired
    private WebApplicationContext context;

    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    private String token(String json, String path) throws Exception {
        MvcResult result = mvc.perform(post(path).contentType(MediaType.APPLICATION_JSON)
                .content(json)).andReturn();
        return result.getResponse().getContentAsString()
                .replaceAll(".*\"token\":\"([^\"]+)\".*", "$1");
    }

    private String productId(String name) throws Exception {
        MvcResult result = mvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"" + name + "\",\"price\":1000,"
                        + "\"imageUrl\":\"https://example.com/a.png\"}"))
                .andExpect(status().isCreated()).andReturn();
        return result.getResponse().getContentAsString()
                .replaceAll(".*\"id\":(\\d+).*", "$1");
    }

    @Test
    @DisplayName("토큰 없이 위시 리스트를 요청하면 401이다")
    void wishesRequireToken() throws Exception {
        mvc.perform(get("/api/wishes")).andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("상품 API는 토큰 없이도 사용할 수 있다")
    void productsArePublic() throws Exception {
        mvc.perform(get("/api/products")).andExpect(status().isOk());
    }

    @Test
    @DisplayName("가입, 상품 등록, 위시 추가, 조회, 삭제가 실제 설정으로 동작한다")
    void fullFlow() throws Exception {
        String bearer = "Bearer " + token(
                "{\"email\":\"flow@gmail.com\",\"password\":\"abcd123!\"}",
                "/api/members/register");
        String id = productId("FlowItem");

        mvc.perform(post("/api/wishes").header("Authorization", bearer)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"productId\":" + id + "}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.product.name").value("FlowItem"));
        mvc.perform(get("/api/wishes").header("Authorization", bearer))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].product.name").value("FlowItem"));
        mvc.perform(delete("/api/wishes/" + id).header("Authorization", bearer))
                .andExpect(status().isNoContent());
        mvc.perform(get("/api/wishes").header("Authorization", bearer))
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    @DisplayName("로그인으로 받은 토큰으로도 위시 리스트를 사용할 수 있다")
    void loginTokenWorks() throws Exception {
        token("{\"email\":\"login@gmail.com\",\"password\":\"abcd123!\"}",
                "/api/members/register");
        String bearer = "Bearer " + token(
                "{\"email\":\"login@gmail.com\",\"password\":\"abcd123!\"}",
                "/api/members/login");

        mvc.perform(get("/api/wishes").header("Authorization", bearer))
                .andExpect(status().isOk());
        assertThat(bearer).startsWith("Bearer ");
    }
}
