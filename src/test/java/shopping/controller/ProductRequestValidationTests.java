package shopping.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import shopping.dto.ProductRequest;
import shopping.service.JwtProvider;
import shopping.service.ProductService;
import tools.jackson.databind.ObjectMapper;

import java.util.stream.Stream;

import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductController.class)
class ProductRequestValidationTests {

    private static final String IMAGE_URL = "https://a.com/a.png";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ProductService productService;

    @MockitoBean
    private JwtProvider jwtProvider;

    static Stream<ProductRequest> invalidRequests() {
        return Stream.of(
                new ProductRequest(null, 1000, IMAGE_URL),
                new ProductRequest("", 1000, IMAGE_URL),
                new ProductRequest("   ", 1000, IMAGE_URL),
                new ProductRequest("치킨", null, IMAGE_URL),
                new ProductRequest("치킨", -1, IMAGE_URL),
                new ProductRequest("치킨", 1000, null),
                new ProductRequest("치킨", 1000, "")
        );
    }

    @DisplayName("필수 값이 없거나 올바르지 않으면 400을 응답하고 서비스를 호출하지 않는다")
    @ParameterizedTest
    @MethodSource("invalidRequests")
    void invalidRequest(ProductRequest request) throws Exception {
        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").isNotEmpty());

        verifyNoInteractions(productService);
    }
}
