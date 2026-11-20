package shopping.support;

import com.jayway.jsonpath.JsonPath;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.test.web.servlet.assertj.MvcTestResult;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest
@Transactional
@Import(FakeProfanityCheckerConfig.class)
public abstract class ApiTest {

    protected static final String IMAGE_URL = "https://example.com/image.png";
    private static final String PASSWORD = "password123";

    @Autowired
    private WebApplicationContext context;

    protected MockMvcTester mvc;

    @BeforeEach
    void setUpMockMvc() {
        mvc = MockMvcTester.from(context, builder -> builder
                .defaultResponseCharacterEncoding(StandardCharsets.UTF_8)
                .build());
    }

    protected MvcTestResult post(String uri, String json) {
        return mvc.post().uri(uri).contentType(MediaType.APPLICATION_JSON).content(json).exchange();
    }

    protected MvcTestResult postWithToken(String uri, String token, String json) {
        return mvc.post().uri(uri)
                .header(HttpHeaders.AUTHORIZATION, bearer(token))
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .exchange();
    }

    protected String bearer(String token) {
        return "Bearer " + token;
    }

    protected String memberJson(String email, String password) {
        return """
                {"email": "%s", "password": "%s"}
                """.formatted(email, password);
    }

    protected String productJson(String name, int price, String imageUrl) {
        return """
                {"name": "%s", "price": %d, "imageUrl": "%s"}
                """.formatted(name, price, imageUrl);
    }

    protected String registerAndGetToken(String email) {
        MvcTestResult result = post("/api/members/register", memberJson(email, PASSWORD));
        return JsonPath.read(body(result), "$.token");
    }

    protected Long createProduct(String name) {
        MvcTestResult result = post("/api/products", productJson(name, 1000, IMAGE_URL));
        return readId(result);
    }

    protected Long readId(MvcTestResult result) {
        Number id = JsonPath.read(body(result), "$.id");
        return id.longValue();
    }

    private String body(MvcTestResult result) {
        try {
            return result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException(e);
        }
    }
}
