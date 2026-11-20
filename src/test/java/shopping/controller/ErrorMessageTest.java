package shopping.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import shopping.domain.Members;
import shopping.domain.Products;
import shopping.service.AuthService;
import shopping.service.MemberService;
import shopping.service.ProductService;
import shopping.service.TokenStore;
import shopping.service.WishService;

/** IT를 모르는 사용자가 읽는 오류 메시지가 쉬운 말로 나오는지 확인한다. */
class ErrorMessageTest {
    private static final List<String> JARGON = List.of("null", "ID", "id=", "URL", "JSON",
            "Exception", "토큰");

    private MockMvc mvc;
    private String bearer;

    @BeforeEach
    void setUp() throws Exception {
        Members members = new Members();
        TokenStore tokenStore = new TokenStore();
        MemberService memberService = new MemberService(members, tokenStore);
        ProductService productService = new ProductService(new Products(), text -> false);
        mvc = MockMvcBuilders.standaloneSetup(new ProductController(productService),
                        new MemberController(memberService),
                        new WishController(new WishService(productService)))
                .setCustomArgumentResolvers(
                        new LoginMemberArgumentResolver(new AuthService(members, tokenStore)))
                .setControllerAdvice(new ApiExceptionHandler())
                .build();
        bearer = "Bearer " + memberService.register("user@gmail.com", "abcd123!");
        productService.create("Same", 1000, "https://example.com/a.png");
    }

    private ResultActions send(String path, String json) throws Exception {
        return mvc.perform(post(path).header("Authorization", bearer)
                .contentType(MediaType.APPLICATION_JSON).content(json));
    }

    private static String product(String name, String price, String image) {
        return "{\"name\":" + name + ",\"price\":" + price + ",\"imageUrl\":" + image + "}";
    }

    private static String member(String email, String password) {
        return "{\"email\":\"" + email + "\",\"password\":\"" + password + "\"}";
    }

    private static final String OK_IMAGE = "\"https://example.com/a.png\"";

    static Stream<Arguments> postCases() {
        return Stream.of(
                Arguments.of("/api/products", product("\"bad!\"", "1", OK_IMAGE), 400,
                        "상품 이름에는 영문, 숫자, 띄어쓰기와 특수문자 ( ) [ ] + - & / _ 만 사용할 수 있습니다."),
                Arguments.of("/api/products", product("\"\"", "1", OK_IMAGE), 400,
                        "상품 이름을 입력해 주세요."),
                Arguments.of("/api/products", product("null", "1", OK_IMAGE), 400,
                        "상품 이름을 입력해 주세요."),
                Arguments.of("/api/products", product("\"abcde fghij klmn\"", "1", OK_IMAGE), 400,
                        "상품 이름은 공백 포함 15자 이하여야 합니다."),
                Arguments.of("/api/products", product("\"Same\"", "1", OK_IMAGE), 400,
                        "이미 같은 이름의 상품이 있습니다: Same"),
                Arguments.of("/api/products", product("\"Item\"", "-1", OK_IMAGE), 400,
                        "상품 가격은 0원 이상이어야 합니다."),
                Arguments.of("/api/products", product("\"Item\"", "null", OK_IMAGE), 400,
                        "상품 가격을 입력해 주세요."),
                Arguments.of("/api/products", product("\"Item\"", "1.5", OK_IMAGE), 400,
                        "상품 가격은 소수가 아닌 숫자로 입력해 주세요."),
                Arguments.of("/api/products", product("\"Item\"", "1", "\"http://a.com/a.png\""),
                        400, "상품 이미지 주소는 https://로 시작해야 합니다."),
                Arguments.of("/api/products", product("\"Item\"", "1", "\"\""), 400,
                        "상품 이미지 주소를 입력해 주세요."),
                Arguments.of("/api/products", product("\"Item\"", "1", "\"https://\""), 400,
                        "상품 이미지 주소가 올바르지 않습니다. (예: https://example.com/image.png)"),
                Arguments.of("/api/products", "not json", 400,
                        "요청 내용을 읽을 수 없습니다. 입력한 내용을 확인해 주세요."),
                Arguments.of("/api/members/register", member("", "abcd123!"), 400,
                        "이메일을 입력해 주세요."),
                Arguments.of("/api/members/register", member("user", "abcd123!"), 400,
                        "이메일 형식이 올바르지 않습니다. @가 하나만 있어야 합니다."),
                Arguments.of("/api/members/register", member("@gmail.com", "abcd123!"), 400,
                        "이메일의 @ 앞에 아이디를 입력해 주세요."),
                Arguments.of("/api/members/register", member("user@naver.com", "abcd123!"), 400,
                        "gmail.com 이메일만 사용할 수 있습니다."),
                Arguments.of("/api/members/register", member(" user@gmail.com", "abcd123!"), 400,
                        "이메일에는 띄어쓰기를 사용할 수 없습니다."),
                Arguments.of("/api/members/register", member("user@gmail.com", "abcd123!"), 400,
                        "이미 가입된 이메일입니다."),
                Arguments.of("/api/members/register", member("new@gmail.com", ""), 400,
                        "비밀번호를 입력해 주세요."),
                Arguments.of("/api/members/register", member("new@gmail.com", "short1!"), 400,
                        "비밀번호는 8자 이상이어야 합니다."),
                Arguments.of("/api/members/register", member("new@gmail.com", "12345678!"), 400,
                        "비밀번호에는 영문이 하나 이상 있어야 합니다."),
                Arguments.of("/api/members/register", member("new@gmail.com", "abcdefg!"), 400,
                        "비밀번호에는 숫자가 하나 이상 있어야 합니다."),
                Arguments.of("/api/members/register", member("new@gmail.com", "abcd1234"), 400,
                        "비밀번호에는 특수문자가 하나 이상 있어야 합니다."),
                Arguments.of("/api/members/register", member("new@gmail.com", "abcd 123!"), 400,
                        "비밀번호에는 영문, 숫자, 특수문자(!, @ 등)만 사용할 수 있습니다. "
                                + "띄어쓰기와 한글은 사용할 수 없습니다."),
                Arguments.of("/api/members/register", "{}", 400,
                        "이메일과 비밀번호를 입력해 주세요."),
                Arguments.of("/api/members/login", member("user@gmail.com", "zzzz9999!"), 401,
                        "이메일 또는 비밀번호가 올바르지 않습니다."),
                Arguments.of("/api/wishes", "{}", 400, "상품 번호를 입력해 주세요."),
                Arguments.of("/api/wishes", "{\"productId\":0}", 400,
                        "상품 번호는 1 이상의 숫자여야 합니다."),
                Arguments.of("/api/wishes", "{\"productId\":1.5}", 400,
                        "상품 번호는 숫자(정수)여야 합니다."),
                Arguments.of("/api/wishes", "{\"productId\":99}", 404,
                        "존재하지 않는 상품입니다. (상품 번호: 99)"));
    }

    @ParameterizedTest
    @MethodSource("postCases")
    @DisplayName("오류 메시지는 쉬운 말로 알려준다")
    void plainMessages(String path, String json, int status, String message) throws Exception {
        send(path, json)
                .andExpect(status().is(status))
                .andExpect(jsonPath("$.message").value(message));
    }

    @ParameterizedTest
    @MethodSource("postCases")
    @DisplayName("오류 메시지에 개발 용어(null, ID, URL, JSON 등)가 없다")
    void noJargon(String path, String json, int status, String message) {
        assertThat(JARGON).noneMatch(message::contains);
    }

    @Test
    @DisplayName("조회, 삭제, 인증 오류도 쉬운 말로 알려준다")
    void otherMessages() throws Exception {
        mvc.perform(get("/api/products/99")).andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("존재하지 않는 상품입니다. (상품 번호: 99)"));
        mvc.perform(get("/api/products/0")).andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("상품 번호는 1 이상의 숫자여야 합니다."));
        mvc.perform(get("/api/products/abc")).andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("주소에 입력한 번호가 올바르지 않습니다."));
        mvc.perform(delete("/api/wishes/1").header("Authorization", bearer))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("위시 리스트에 없는 상품입니다. (상품 번호: 1)"));
        mvc.perform(get("/api/wishes")).andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("로그인이 필요합니다. 먼저 로그인해 주세요."));
    }

    @Test
    @DisplayName("이미 담은 상품을 다시 담으면 쉬운 말로 알려준다")
    void duplicateWish() throws Exception {
        send("/api/wishes", "{\"productId\":1}").andExpect(status().isCreated());

        send("/api/wishes", "{\"productId\":1}").andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("이미 위시 리스트에 담은 상품입니다."));
    }
}
