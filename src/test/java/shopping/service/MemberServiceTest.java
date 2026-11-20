package shopping.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import shopping.domain.Email;
import shopping.domain.MemberId;
import shopping.domain.Members;

class MemberServiceTest {
    private static final String EMAIL = "user@gmail.com";
    private static final String PASSWORD = "abcd123!";

    private TokenStore tokenStore;
    private Members members;
    private MemberService service;

    @BeforeEach
    void setUp() {
        tokenStore = new TokenStore();
        members = new Members();
        service = new MemberService(members, tokenStore);
    }

    @Test
    @DisplayName("가입하면 토큰을 돌려주고 그 토큰은 가입한 이메일의 것이다")
    void register() {
        String token = service.register(EMAIL, PASSWORD);

        assertThat(tokenStore.find(token)).contains(new MemberId(1));
    }

    @Test
    @DisplayName("같은 이메일로 다시 가입할 수 없다")
    void registerDuplicateEmail() {
        service.register(EMAIL, PASSWORD);

        assertThatThrownBy(() -> service.register(EMAIL, "zzzz9999!"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"user", "user@naver.com", "user@@gmail.com", " user@gmail.com"})
    @DisplayName("이메일 규칙을 어기면 가입할 수 없다")
    void registerInvalidEmail(String email) {
        assertThatThrownBy(() -> service.register(email, PASSWORD))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"short1!", "abcdefgh!", "12345678!", "abcd1234", "abcd 123!"})
    @DisplayName("비밀번호 규칙을 어기면 가입할 수 없다")
    void registerInvalidPassword(String password) {
        assertThatThrownBy(() -> service.register(EMAIL, password))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("규칙을 어겨 가입에 실패하면 이후 같은 이메일로 가입할 수 있다")
    void registerFailureLeavesNothing() {
        assertThatThrownBy(() -> service.register(EMAIL, "short"))
                .isInstanceOf(IllegalArgumentException.class);

        assertThat(tokenStore.find(service.register(EMAIL, PASSWORD))).isPresent();
    }

    @Test
    @DisplayName("이메일과 비밀번호가 맞으면 로그인되어 토큰을 돌려준다")
    void login() {
        service.register(EMAIL, PASSWORD);

        String token = service.login(EMAIL, PASSWORD);

        assertThat(tokenStore.find(token)).contains(new MemberId(1));
    }

    @Test
    @DisplayName("로그인할 때마다 새 토큰이 발급된다")
    void loginIssuesNewToken() {
        String registered = service.register(EMAIL, PASSWORD);

        String first = service.login(EMAIL, PASSWORD);
        String second = service.login(EMAIL, PASSWORD);

        assertThat(first).isNotEqualTo(registered).isNotEqualTo(second);
    }

    @Test
    @DisplayName("비밀번호가 틀리면 로그인할 수 없다")
    void loginWrongPassword() {
        service.register(EMAIL, PASSWORD);

        assertThatThrownBy(() -> service.login(EMAIL, "zzzz9999!"))
                .isInstanceOf(LoginFailedException.class);
    }

    @Test
    @DisplayName("가입하지 않은 이메일로는 로그인할 수 없다")
    void loginUnknownEmail() {
        assertThatThrownBy(() -> service.login("none@gmail.com", PASSWORD))
                .isInstanceOf(LoginFailedException.class);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"user", "user@naver.com", "short"})
    @DisplayName("형식이 올바르지 않은 이메일이나 비밀번호로 로그인하면 같은 실패로 처리한다")
    void loginInvalidFormat(String value) {
        service.register(EMAIL, PASSWORD);

        assertThatThrownBy(() -> service.login(value, PASSWORD))
                .isInstanceOf(LoginFailedException.class);
        assertThatThrownBy(() -> service.login(EMAIL, value))
                .isInstanceOf(LoginFailedException.class);
    }

    @Test
    @DisplayName("로그인 실패 메시지는 이메일 없음과 비밀번호 틀림을 구분하지 않는다")
    void loginFailureMessageIsSame() {
        service.register(EMAIL, PASSWORD);

        String wrongPassword = messageOf(() -> service.login(EMAIL, "zzzz9999!"));
        String unknownEmail = messageOf(() -> service.login("none@gmail.com", PASSWORD));

        assertThat(wrongPassword).isEqualTo(unknownEmail);
    }

    @Test
    @DisplayName("비밀번호는 대소문자를 구분한다")
    void passwordIsCaseSensitive() {
        service.register(EMAIL, "Abcd123!");

        assertThatThrownBy(() -> service.login(EMAIL, "abcd123!"))
                .isInstanceOf(LoginFailedException.class);
    }

    private static String messageOf(Runnable action) {
        try {
            action.run();
        } catch (LoginFailedException e) {
            return e.getMessage();
        }
        throw new AssertionError("로그인 실패가 발생하지 않았다");
    }

    @Test
    @DisplayName("가입한 회원에게 1부터 순서대로 ID가 부여되고 ID로 찾을 수 있다")
    void registerAssignsIds() {
        String first = service.register("first@gmail.com", PASSWORD);
        String second = service.register("second@gmail.com", PASSWORD);

        assertThat(tokenStore.find(first)).contains(new MemberId(1));
        assertThat(tokenStore.find(second)).contains(new MemberId(2));
        assertThat(members.findById(new MemberId(2)).orElseThrow().email())
                .isEqualTo(new Email("second@gmail.com"));
    }

    @Test
    @DisplayName("가입에 실패하면 ID를 소비하지 않아 번호가 건너뛰지 않는다")
    void failedRegisterDoesNotConsumeId() {
        service.register(EMAIL, PASSWORD);
        assertThatThrownBy(() -> service.register(EMAIL, PASSWORD))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> service.register("other@gmail.com", "short"))
                .isInstanceOf(IllegalArgumentException.class);

        String token = service.register("third@gmail.com", PASSWORD);

        assertThat(tokenStore.find(token)).contains(new MemberId(2));
    }

    @Test
    @DisplayName("로그인으로 받은 토큰은 가입한 회원의 ID를 가리킨다")
    void loginTokenPointsToMemberId() {
        service.register("first@gmail.com", PASSWORD);
        service.register("second@gmail.com", PASSWORD);

        String token = service.login("second@gmail.com", PASSWORD);

        assertThat(tokenStore.find(token)).contains(new MemberId(2));
    }
}
