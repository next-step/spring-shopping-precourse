package shopping.member.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import shopping.auth.application.TokenIssuer;
import shopping.auth.domain.AccessToken;
import shopping.common.error.ErrorCode;
import shopping.common.error.ShoppingException;
import shopping.member.domain.Email;
import shopping.member.domain.Member;
import shopping.member.domain.Password;
import shopping.member.persistence.MemberRepository;

@DisplayName("회원 서비스")
class MemberServiceTest {

    private final MemberRepository members = mock(MemberRepository.class);
    private final TokenIssuer tokens = mock(TokenIssuer.class);
    private final MemberService service = new MemberService(members, tokens);
    private final Email email = new Email("yeji@example.com");

    @DisplayName("저장된 회원의 ID로 가입 토큰을 발급한다")
    @Test
    void issuesTokenForSavedMember() {
        Member saved = mock(Member.class);
        AccessToken token = AccessToken.issue("issued", 7L, Instant.EPOCH);
        when(saved.id()).thenReturn(7L);
        when(members.saveAndFlush(any(Member.class))).thenReturn(saved);
        when(tokens.issue(7L)).thenReturn(token);

        assertThat(service.register(" yeji@example.com ", "hello123")).isSameAs(token);
        verify(members).existsByEmail(new Email("yeji@example.com"));
    }

    @DisplayName("중복 이메일로 가입하면 회원 저장과 토큰 발급을 하지 않는다")
    @Test
    void duplicateEmailDoesNotSaveOrIssueToken() {
        when(members.existsByEmail(new Email("yeji@example.com"))).thenReturn(true);

        assertThatThrownBy(() -> service.register("yeji@example.com", "hello123"))
                .isInstanceOfSatisfying(ShoppingException.class,
                        error -> assertThat(error.code())
                                .isEqualTo(ErrorCode.EMAIL_ALREADY_EXISTS));
        verify(members, never()).saveAndFlush(any());
        verifyNoInteractions(tokens);
    }

    @DisplayName("회원의 비밀번호 인증에 성공하면 토큰을 발급한다")
    @Test
    void issuesTokenAfterMemberAuthentication() {
        Member member = mock(Member.class);
        AccessToken issued = AccessToken.issue("new-token", 7L, Instant.EPOCH);
        when(members.findByEmail(email)).thenReturn(Optional.of(member));
        when(member.id()).thenReturn(7L);
        when(tokens.issue(7L)).thenReturn(issued);

        assertThat(service.login(" yeji@example.com ", "hello123")).isSameAs(issued);
        verify(member).authenticate(argThat(value -> value.matches(new Password("hello123"))));
    }

    @DisplayName("미가입 이메일로 로그인하면 토큰을 발급하지 않는다")
    @Test
    void missingMemberDoesNotIssueToken() {
        when(members.findByEmail(email)).thenReturn(Optional.empty());

        assertInvalidLogin();
        verifyNoInteractions(tokens);
    }

    @DisplayName("비밀번호가 다르면 토큰을 발급하지 않는다")
    @Test
    void incorrectPasswordDoesNotIssueToken() {
        Member member = new Member(email, new Password("different"));
        when(members.findByEmail(email)).thenReturn(Optional.of(member));

        assertInvalidLogin();
        verifyNoInteractions(tokens);
    }

    private void assertInvalidLogin() {
        assertThatThrownBy(() -> service.login(email.value(), "hello123"))
                .isInstanceOfSatisfying(ShoppingException.class,
                        error -> assertThat(error.code()).isEqualTo(ErrorCode.INVALID_CREDENTIALS));
    }
}
