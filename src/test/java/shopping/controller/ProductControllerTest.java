package shopping.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
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
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import shopping.domain.Products;
import shopping.service.ProductService;

class ProductControllerTest {
    private static final String URL = "https://example.com/a.png";

    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        ProductService service = new ProductService(new Products(), text -> text.contains("shit"));
        mvc = MockMvcBuilders.standaloneSetup(new ProductController(service))
                .setControllerAdvice(new ApiExceptionHandler())
                .build();
    }

    private ResultActions create(String json) throws Exception {
        return mvc.perform(post("/api/products").contentType(MediaType.APPLICATION_JSON)
                .content(json));
    }

    private static String body(String name, String price, String imageUrl) {
        return "{\"name\":" + name + ",\"price\":" + price + ",\"imageUrl\":" + imageUrl + "}";
    }

    private static String valid(String name) {
        return body("\"" + name + "\"", "1000", "\"" + URL + "\"");
    }

    @Test
    @DisplayName("상품을 생성하면 201과 Location, 생성된 상품을 돌려준다")
    void createProduct() throws Exception {
        create(valid("Galaxy S24"))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/products/1"))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Galaxy S24"))
                .andExpect(jsonPath("$.price").value(1000))
                .andExpect(jsonPath("$.imageUrl").value(URL));
    }

    @Test
    @DisplayName("ID는 1부터 순서대로 부여된다")
    void createAssignsIds() throws Exception {
        create(valid("First")).andExpect(jsonPath("$.id").value(1));
        create(valid("Second")).andExpect(jsonPath("$.id").value(2));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "{\"name\":\"bad!\",\"price\":1000,\"imageUrl\":\"https://example.com/a.png\"}",
            "{\"name\":\"Item\",\"price\":-1,\"imageUrl\":\"https://example.com/a.png\"}",
            "{\"name\":\"Item\",\"price\":1000,\"imageUrl\":\"http://example.com/a.png\"}",
            "{\"name\":\"shit\",\"price\":1000,\"imageUrl\":\"https://example.com/a.png\"}",
            "{\"name\":\"\",\"price\":1000,\"imageUrl\":\"https://example.com/a.png\"}",
            "{\"name\":\"abcde fghij klmn\",\"price\":1000,\"imageUrl\":\"https://a.com\"}"})
    @DisplayName("규칙을 어긴 값으로 생성하면 400과 message를 돌려준다")
    void createInvalid(String json) throws Exception {
        create(json)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").isNotEmpty());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "{\"price\":1000,\"imageUrl\":\"https://example.com/a.png\"}",
            "{\"name\":\"Item\",\"imageUrl\":\"https://example.com/a.png\"}",
            "{\"name\":\"Item\",\"price\":1000}",
            "{\"name\":null,\"price\":null,\"imageUrl\":null}",
            "{}"})
    @DisplayName("필수 값이 빠지면 400을 돌려준다")
    void createMissingField(String json) throws Exception {
        create(json)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").isNotEmpty());
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "not json", "{", "[]",
            "{\"name\":\"Item\",\"price\":\"abc\",\"imageUrl\":\"https://a.com\"}",
            "{\"name\":\"Item\",\"price\":1.5,\"imageUrl\":\"https://a.com\"}",
            "{\"name\":\"Item\",\"price\":99999999999999999999,\"imageUrl\":\"https://a.com\"}"})
    @DisplayName("JSON 형식이 올바르지 않으면 400을 돌려준다")
    void createMalformedJson(String json) throws Exception {
        create(json)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").isNotEmpty());
    }

    @Test
    @DisplayName("같은 이름의 상품을 만들면 400을 돌려준다")
    void createDuplicateName() throws Exception {
        create(valid("Same")).andExpect(status().isCreated());

        create(valid("Same"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").isNotEmpty());
    }

    @Test
    @DisplayName("ID로 상품을 조회한다")
    void getProduct() throws Exception {
        create(valid("Galaxy S24"));

        mvc.perform(get("/api/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Galaxy S24"))
                .andExpect(jsonPath("$.price").value(1000))
                .andExpect(jsonPath("$.imageUrl").value(URL));
    }

    @Test
    @DisplayName("없는 상품을 조회하면 404를 돌려준다")
    void getNotFound() throws Exception {
        mvc.perform(get("/api/products/9"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").isNotEmpty());
    }

    @ParameterizedTest
    @ValueSource(strings = {"0", "-1", "abc", "1.5"})
    @DisplayName("상품 ID가 올바르지 않으면 400을 돌려준다")
    void getInvalidId(String id) throws Exception {
        mvc.perform(get("/api/products/" + id))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").isNotEmpty());
    }

    @Test
    @DisplayName("상품 목록을 생성한 순서대로 조회한다")
    void listProducts() throws Exception {
        create(valid("First"));
        create(valid("Second"));

        mvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("First"))
                .andExpect(jsonPath("$[1].name").value("Second"));
    }

    @Test
    @DisplayName("상품이 없으면 빈 배열을 돌려준다")
    void listEmpty() throws Exception {
        mvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    @DisplayName("상품을 수정하면 200과 수정된 상품을 돌려준다")
    void updateProduct() throws Exception {
        create(valid("First"));

        mvc.perform(put("/api/products/1").contentType(MediaType.APPLICATION_JSON)
                        .content(body("\"Renamed\"", "5", "\"https://b.com/b.png\"")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Renamed"))
                .andExpect(jsonPath("$.price").value(5))
                .andExpect(jsonPath("$.imageUrl").value("https://b.com/b.png"));
    }

    @Test
    @DisplayName("수정에도 같은 규칙이 적용되고 실패하면 기존 값이 유지된다")
    void updateInvalid() throws Exception {
        create(valid("First"));

        mvc.perform(put("/api/products/1").contentType(MediaType.APPLICATION_JSON)
                        .content(body("\"bad!\"", "5", "\"" + URL + "\"")))
                .andExpect(status().isBadRequest());
        mvc.perform(put("/api/products/1").contentType(MediaType.APPLICATION_JSON)
                        .content(body("\"First\"", "-5", "\"" + URL + "\"")))
                .andExpect(status().isBadRequest());

        mvc.perform(get("/api/products/1"))
                .andExpect(jsonPath("$.name").value("First"))
                .andExpect(jsonPath("$.price").value(1000));
    }

    @Test
    @DisplayName("다른 상품과 같은 이름으로 수정하면 400을 돌려준다")
    void updateDuplicateName() throws Exception {
        create(valid("First"));
        create(valid("Second"));

        mvc.perform(put("/api/products/2").contentType(MediaType.APPLICATION_JSON)
                        .content(valid("First")))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("자기 자신의 이름으로 수정하는 것은 허용한다")
    void updateWithOwnName() throws Exception {
        create(valid("First"));

        mvc.perform(put("/api/products/1").contentType(MediaType.APPLICATION_JSON)
                        .content(body("\"First\"", "7", "\"" + URL + "\"")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.price").value(7));
    }

    @Test
    @DisplayName("없는 상품을 수정하면 404를 돌려준다")
    void updateNotFound() throws Exception {
        mvc.perform(put("/api/products/9").contentType(MediaType.APPLICATION_JSON)
                        .content(valid("Item")))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("상품을 삭제하면 204를 돌려주고 이후 조회되지 않는다")
    void deleteProduct() throws Exception {
        create(valid("First"));

        mvc.perform(delete("/api/products/1")).andExpect(status().isNoContent());

        mvc.perform(get("/api/products/1")).andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("없는 상품을 삭제하면 404를 돌려준다")
    void deleteNotFound() throws Exception {
        mvc.perform(delete("/api/products/9"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").isNotEmpty());
    }
}
