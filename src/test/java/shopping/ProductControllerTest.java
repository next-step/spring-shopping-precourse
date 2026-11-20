package shopping;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@WebMvcTest(ProductController.class)
class ProductControllerTest {
    @MockitoBean private MemberAuthenticator memberAuthenticator;

    @Autowired private MockMvc mockMvc;

    @MockitoBean private ProductService productService;

    @Test
    void createReturnsCreatedProduct() throws Exception {
        Product product = savedProduct();
        given(productService.create("키보드", 50000, "https://example.com/keyboard.jpg"))
                .willReturn(product);

        ResultActions result =
                mockMvc.perform(
                                post("/api/products")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(requestBody()))
                        .andExpect(status().isCreated())
                        .andExpect(header().string("Location", "/api/products/1"));
        assertKeyboardResponse(result);
    }

    private void assertKeyboardResponse(ResultActions result) throws Exception {
        result.andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("키보드"))
                .andExpect(jsonPath("$.price").value(50000))
                .andExpect(jsonPath("$.imageUrl").value("https://example.com/keyboard.jpg"));
    }

    private Product savedProduct() {
        Product product = mock(Product.class);
        given(product.getId()).willReturn(1L);
        given(product.getName()).willReturn("키보드");
        given(product.getPrice()).willReturn(50000);
        given(product.getImageUrl()).willReturn("https://example.com/keyboard.jpg");
        return product;
    }

    private String requestBody() {
        return """
               {
                 "name": "키보드",
                 "price": 50000,
                 "imageUrl": "https://example.com/keyboard.jpg"
               }
               """;
    }

    @Test
    void findByIdReturnsProduct() throws Exception {
        Product product = savedProduct();
        given(productService.findById(1L)).willReturn(product);

        mockMvc.perform(get("/api/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("키보드"))
                .andExpect(jsonPath("$.price").value(50000))
                .andExpect(jsonPath("$.imageUrl").value("https://example.com/keyboard.jpg"));
    }

    @Test
    void findByIdReturnsNotFound() throws Exception {
        given(productService.findById(-1L)).willThrow(new ProductNotFoundException(-1L));

        mockMvc.perform(get("/api/products/-1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("PRODUCT_NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("상품을 찾을 수 없습니다. id=-1"));
    }

    @Test
    void findAllReturnsProducts() throws Exception {
        Product product = savedProduct();
        given(productService.findAll()).willReturn(List.of(product));

        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("키보드"))
                .andExpect(jsonPath("$[0].price").value(50000))
                .andExpect(jsonPath("$[0].imageUrl").value("https://example.com/keyboard.jpg"));
    }

    @Test
    void findAllReturnsEmptyArray() throws Exception {
        given(productService.findAll()).willReturn(List.of());

        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void updateReturnsUpdatedProduct() throws Exception {
        Product product = savedProduct();
        given(productService.update(1L, "키보드", 50000, "https://example.com/keyboard.jpg"))
                .willReturn(product);

        mockMvc.perform(
                        put("/api/products/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("키보드"))
                .andExpect(jsonPath("$.price").value(50000))
                .andExpect(jsonPath("$.imageUrl").value("https://example.com/keyboard.jpg"));
    }

    @Test
    void updateReturnsNotFound() throws Exception {
        given(productService.update(-1L, "키보드", 50000, "https://example.com/keyboard.jpg"))
                .willThrow(new ProductNotFoundException(-1L));

        mockMvc.perform(
                        put("/api/products/-1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody()))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteReturnsNoContent() throws Exception {
        mockMvc.perform(delete("/api/products/1"))
                .andExpect(status().isNoContent())
                .andExpect(jsonPath("$").doesNotExist());

        verify(productService).delete(1L);
    }

    @Test
    void deleteReturnsNotFound() throws Exception {
        willThrow(new ProductNotFoundException(-1L)).given(productService).delete(-1L);

        mockMvc.perform(delete("/api/products/-1")).andExpect(status().isNotFound());
    }

    @ParameterizedTest
    @MethodSource("invalidProductRequests")
    void createRejectsInvalidInput(String body) throws Exception {
        mockMvc.perform(post("/api/products").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_REQUEST"))
                .andExpect(jsonPath("$.message").isNotEmpty());

        verifyNoInteractions(productService);
    }

    @ParameterizedTest
    @MethodSource("invalidProductRequests")
    void updateRejectsInvalidInput(String body) throws Exception {
        mockMvc.perform(
                        put("/api/products/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_REQUEST"))
                .andExpect(jsonPath("$.message").isNotEmpty());

        verifyNoInteractions(productService);
    }

    @ParameterizedTest
    @CsvSource({
        "'()[]+-&/_', http://example.com/a.jpg",
        "abcdefghijklmno, https://example.com/a.jpg",
        "'상품 A', https://example.com/a.jpg"
    })
    void createAcceptsValidFormat(String name, String imageUrl) throws Exception {
        Product product = savedProduct();
        given(productService.create(name, 0, imageUrl)).willReturn(product);
        String body = productJson("\"" + name + "\"", "0", "\"" + imageUrl + "\"");

        mockMvc.perform(post("/api/products").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated());

        verify(productService).create(name, 0, imageUrl);
    }

    @ParameterizedTest
    @CsvSource({
        "'()[]+-&/_', http://example.com/a.jpg",
        "abcdefghijklmno, https://example.com/a.jpg",
        "'상품 A', https://example.com/a.jpg"
    })
    void updateAcceptsValidFormat(String name, String imageUrl) throws Exception {
        Product product = savedProduct();
        given(productService.update(1L, name, 0, imageUrl)).willReturn(product);
        String body = productJson("\"" + name + "\"", "0", "\"" + imageUrl + "\"");

        mockMvc.perform(
                        put("/api/products/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(body))
                .andExpect(status().isOk());

        verify(productService).update(1L, name, 0, imageUrl);
    }

    @ParameterizedTest
    @ValueSource(strings = {"{", "{\"name\":\"keyboard\",\"price\":\"abc\"}"})
    void createRejectsUnreadableJson(String body) throws Exception {
        mockMvc.perform(post("/api/products").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_REQUEST"))
                .andExpect(jsonPath("$.message").value("요청 JSON 형식이나 값의 타입이 올바르지 않습니다."));

        verifyNoInteractions(productService);
    }

    @ParameterizedTest
    @ValueSource(strings = {"{", "{\"name\":\"keyboard\",\"price\":\"abc\"}"})
    void updateRejectsUnreadableJson(String body) throws Exception {
        mockMvc.perform(
                        put("/api/products/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_REQUEST"))
                .andExpect(jsonPath("$.message").value("요청 JSON 형식이나 값의 타입이 올바르지 않습니다."));

        verifyNoInteractions(productService);
    }

    @ParameterizedTest
    @CsvSource({
        "'  keyboard  ', keyboard",
        "'  상품 A  ', '상품 A'",
        "' abcdefghijklmno ', abcdefghijklmno"
    })
    void createStripsNameBeforeValidation(String raw, String expected) throws Exception {
        Product product = savedProduct();
        String url = "https://example.com/a.jpg";
        given(productService.create(expected, 0, url)).willReturn(product);
        String body = productJson("\"" + raw + "\"", "0", "\"" + url + "\"");
        mockMvc.perform(post("/api/products").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated());
        verify(productService).create(expected, 0, url);
    }

    @ParameterizedTest
    @CsvSource({
        "'  keyboard  ', keyboard",
        "'  상품 A  ', '상품 A'",
        "' abcdefghijklmno ', abcdefghijklmno"
    })
    void updateStripsNameBeforeValidation(String raw, String expected) throws Exception {
        Product product = savedProduct();
        String url = "https://example.com/a.jpg";
        given(productService.update(1L, expected, 0, url)).willReturn(product);
        String body = productJson("\"" + raw + "\"", "0", "\"" + url + "\"");
        mockMvc.perform(
                        put("/api/products/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(body))
                .andExpect(status().isOk());
        verify(productService).update(1L, expected, 0, url);
    }

    private static Stream<String> invalidProductRequests() {
        return Stream.concat(invalidBasicRequests(), invalidFormatRequests());
    }

    private static Stream<String> invalidBasicRequests() {
        return Stream.of(
                productJson("null", "1000", "\"https://example.com/a.jpg\""),
                productJson("\"\"", "1000", "\"https://example.com/a.jpg\""),
                productJson("\"   \"", "1000", "\"https://example.com/a.jpg\""),
                productJson("\"abcdefghijklmnop\"", "1000", "\"https://example.com/a.jpg\""),
                productJson("\" abcdefghijklmnop \"", "1000", "\"https://example.com/a.jpg\""),
                productJson("\"keyboard\"", "null", "\"https://example.com/a.jpg\""),
                productJson("\"keyboard\"", "-1", "\"https://example.com/a.jpg\""),
                productJson("\"keyboard\"", "1000", "null"),
                productJson("\"keyboard\"", "1000", "\"   \""));
    }

    private static Stream<String> invalidFormatRequests() {
        return Stream.of(
                productJson("\"상품@\"", "1000", "\"https://example.com/a.jpg\""),
                productJson("\"특가!\"", "1000", "\"https://example.com/a.jpg\""),
                productJson("\"상품*\"", "1000", "\"https://example.com/a.jpg\""),
                productJson("\"keyboard\"", "1000", "\"not-a-url\""),
                productJson("\"keyboard\"", "1000", "\"ftp://example.com/a.jpg\""),
                productJson("\"keyboard\"", "1000", "\"https://\""));
    }

    private static String productJson(String name, String price, String imageUrl) {
        return """
               {"name": %s, "price": %s, "imageUrl": %s}
               """
                .formatted(name, price, imageUrl);
    }

    @Test
    void createReturnsBadRequestWhenProfanityIsDetected() throws Exception {
        given(productService.create("키보드", 50000, "https://example.com/keyboard.jpg"))
                .willThrow(new ProfanityDetectedException());

        mockMvc.perform(
                        post("/api/products")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("PROFANITY_DETECTED"))
                .andExpect(jsonPath("$.message").value("상품 이름에 비속어를 포함할 수 없습니다."));
    }

    @Test
    void updateReturnsBadRequestWhenProfanityIsDetected() throws Exception {
        given(productService.update(1L, "키보드", 50000, "https://example.com/keyboard.jpg"))
                .willThrow(new ProfanityDetectedException());

        mockMvc.perform(
                        put("/api/products/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("PROFANITY_DETECTED"))
                .andExpect(jsonPath("$.message").value("상품 이름에 비속어를 포함할 수 없습니다."));
    }

    @Test
    void createReturnsUnavailableWhenProfanityServiceFails() throws Exception {
        given(productService.create("키보드", 50000, "https://example.com/keyboard.jpg"))
                .willThrow(new ProfanityServiceUnavailableException());

        mockMvc.perform(
                        post("/api/products")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody()))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.code").value("PROFANITY_SERVICE_UNAVAILABLE"))
                .andExpect(jsonPath("$.message").isNotEmpty());
    }

    @Test
    void updateReturnsUnavailableWhenProfanityServiceFails() throws Exception {
        given(productService.update(1L, "키보드", 50000, "https://example.com/keyboard.jpg"))
                .willThrow(new ProfanityServiceUnavailableException());

        mockMvc.perform(
                        put("/api/products/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody()))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.code").value("PROFANITY_SERVICE_UNAVAILABLE"))
                .andExpect(jsonPath("$.message").isNotEmpty());
    }
}
