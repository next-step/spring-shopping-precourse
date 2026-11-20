package shopping.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import shopping.domain.Email;
import shopping.domain.Member;
import shopping.domain.MemberId;
import shopping.domain.Members;
import shopping.domain.Password;

class AuthServiceTest {
    private TokenStore tokenStore;
    private Members members;
    private AuthService service;
    private Member member;
    private String token;

    @BeforeEach
    void setUp() {
        tokenStore = new TokenStore();
        members = new Members();
        service = new AuthService(members, tokenStore);
        member = new Member(new MemberId(1), new Email("user@gmail.com"), new Password("abcd123!"));
        members.add(member);
        token = tokenStore.issue(new MemberId(1));
    }

    @Test
    @DisplayName("Bearer 토큰이 유효하면 해당 회원을 돌려준다")
    void authenticate() {
        assertThat(service.authenticate("Bearer " + token)).isSameAs(member);
    }

    @Test
    @DisplayName("Bearer는 대소문자를 구분하지 않는다")
    void schemeIsCaseInsensitive() {
        assertThat(service.authenticate("bearer " + token)).isSameAs(member);
        assertThat(service.authenticate("BEARER " + token)).isSameAs(member);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "Bearer", "Bearer ", "Bearer  ", "Basic abc", "token-only",
            "Bearer not-a-token", "Bearer a b", "Bearer\ttoken"})
    @DisplayName("헤더가 없거나 형식이 올바르지 않으면 인증에 실패한다")
    void invalidHeader(String header) {
        assertThatThrownBy(() -> service.authenticate(header))
                .isInstanceOf(UnauthorizedException.class);
    }

    @Test
    @DisplayName("토큰 값만 보내면(Bearer 없이) 인증에 실패한다")
    void tokenWithoutScheme() {
        assertThatThrownBy(() -> service.authenticate(token))
                .isInstanceOf(UnauthorizedException.class);
    }

    @Test
    @DisplayName("발급하지 않은 토큰이면 인증에 실패한다")
    void unknownToken() {
        assertThatThrownBy(() -> service.authenticate("Bearer " + UUID.randomUUID()))
                .isInstanceOf(UnauthorizedException.class);
    }

    @Test
    @DisplayName("토큰은 있지만 해당 회원이 없으면 인증에 실패한다")
    void tokenWithoutMember() {
        String orphan = tokenStore.issue(new MemberId(99));

        assertThatThrownBy(() -> service.authenticate("Bearer " + orphan))
                .isInstanceOf(UnauthorizedException.class);
    }

    @Test
    @DisplayName("인증 실패 메시지는 실패 이유와 상관없이 같다")
    void sameMessage() {
        String missing = messageOf(null);
        String unknown = messageOf("Bearer " + UUID.randomUUID());
        String malformed = messageOf("Basic abc");

        assertThat(missing).isEqualTo(unknown).isEqualTo(malformed);
    }

    private String messageOf(String header) {
        try {
            service.authenticate(header);
        } catch (UnauthorizedException e) {
            return e.getMessage();
        }
        throw new AssertionError("인증 실패가 발생하지 않았다");
    }
}
