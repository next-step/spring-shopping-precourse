package shopping.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.util.ReflectionTestUtils;
import shopping.entity.Member;
import shopping.entity.PasswordEncoder;
import shopping.exception.DuplicateEmailException;
import shopping.exception.InvalidCredentialsException;
import shopping.repository.MemberRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class MemberServiceTests {

    private static final String EMAIL = "user@example.com";
    private static final String PASSWORD = "password123";
    private static final String ENCODED_PASSWORD = "encoded-password";

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtProvider jwtProvider;

    @InjectMocks
    private MemberService memberService;

    @DisplayName("회원 가입하면 인코딩된 비밀번호로 회원을 저장하고 토큰을 발급한다")
    @Test
    void registerSavesMemberAndIssuesToken() {
        given(passwordEncoder.encode(PASSWORD)).willReturn(ENCODED_PASSWORD);
        given(memberRepository.existsByEmail(EMAIL)).willReturn(false);
        given(memberRepository.save(any(Member.class))).willAnswer(invocation -> {
            Member member = invocation.getArgument(0);
            ReflectionTestUtils.setField(member, "id", 1L);
            return member;
        });
        given(jwtProvider.createToken(1L)).willReturn("token");
        given(jwtProvider.getExpiresInSeconds()).willReturn(3600L);

        AuthToken token = memberService.register(EMAIL, PASSWORD);

        ArgumentCaptor<Member> captor = ArgumentCaptor.forClass(Member.class);
        verify(memberRepository).save(captor.capture());
        assertThat(captor.getValue().getEmail()).isEqualTo(EMAIL);
        assertThat(captor.getValue().getPassword()).isEqualTo(ENCODED_PASSWORD);
        assertThat(token.accessToken()).isEqualTo("token");
        assertThat(token.expiresIn()).isEqualTo(3600L);
    }

    @DisplayName("저장 시 유니크 제약 조건을 위반하면 DuplicateEmailException이 발생하고 토큰을 발급하지 않는다")
    @Test
    void registerWithUniqueConstraintViolation() {
        given(memberRepository.existsByEmail(EMAIL)).willReturn(false);
        given(memberRepository.save(any(Member.class))).willThrow(new DataIntegrityViolationException("duplicate"));

        assertThatThrownBy(() -> memberService.register(EMAIL, PASSWORD))
                .isInstanceOf(DuplicateEmailException.class);
        verify(jwtProvider, never()).createToken(any());
    }

    @DisplayName("이미 가입된 이메일이면 DuplicateEmailException이 발생하고 저장하지 않는다")
    @Test
    void registerWithDuplicateEmail() {
        given(memberRepository.existsByEmail(EMAIL)).willReturn(true);

        assertThatThrownBy(() -> memberService.register(EMAIL, PASSWORD))
                .isInstanceOf(DuplicateEmailException.class);
        verify(memberRepository, never()).save(any());
    }

    @DisplayName("대소문자만 다른 이메일도 이미 가입된 이메일로 판단한다")
    @Test
    void registerWithDuplicateEmailIgnoringCase() {
        given(memberRepository.existsByEmail(EMAIL)).willReturn(true);

        assertThatThrownBy(() -> memberService.register("USER@EXAMPLE.COM", PASSWORD))
                .isInstanceOf(DuplicateEmailException.class);
        verify(memberRepository, never()).save(any());
    }

    @DisplayName("이메일과 비밀번호가 일치하면 회원 id로 발급한 토큰을 반환한다")
    @Test
    void loginReturnsToken() {
        Member member = savedMember(1L);
        given(memberRepository.findByEmail(EMAIL)).willReturn(Optional.of(member));
        given(passwordEncoder.matches(PASSWORD, ENCODED_PASSWORD)).willReturn(true);
        given(jwtProvider.createToken(1L)).willReturn("token");
        given(jwtProvider.getExpiresInSeconds()).willReturn(3600L);

        AuthToken token = memberService.login(EMAIL, PASSWORD);

        assertThat(token.accessToken()).isEqualTo("token");
        assertThat(token.expiresIn()).isEqualTo(3600L);
    }

    @DisplayName("로그인 시 이메일은 대소문자를 구분하지 않는다")
    @Test
    void loginIgnoresEmailCase() {
        Member member = savedMember(1L);
        given(memberRepository.findByEmail(EMAIL)).willReturn(Optional.of(member));
        given(passwordEncoder.matches(PASSWORD, ENCODED_PASSWORD)).willReturn(true);
        given(jwtProvider.createToken(1L)).willReturn("token");

        AuthToken token = memberService.login("USER@EXAMPLE.COM", PASSWORD);

        assertThat(token.accessToken()).isEqualTo("token");
    }

    @DisplayName("가입되지 않은 이메일이면 예외가 발생하고 토큰을 발급하지 않는다")
    @Test
    void loginWithUnknownEmail() {
        given(memberRepository.findByEmail(EMAIL)).willReturn(Optional.empty());

        assertThatThrownBy(() -> memberService.login(EMAIL, PASSWORD))
                .isInstanceOf(InvalidCredentialsException.class);
        verify(jwtProvider, never()).createToken(any());
    }

    @DisplayName("비밀번호가 일치하지 않으면 예외가 발생하고 토큰을 발급하지 않는다")
    @Test
    void loginWithWrongPassword() {
        Member member = savedMember(1L);
        given(memberRepository.findByEmail(EMAIL)).willReturn(Optional.of(member));
        given(passwordEncoder.matches("wrong-password", ENCODED_PASSWORD)).willReturn(false);

        assertThatThrownBy(() -> memberService.login(EMAIL, "wrong-password"))
                .isInstanceOf(InvalidCredentialsException.class);
        verify(jwtProvider, never()).createToken(any());
    }

    @DisplayName("이메일이 없는 경우와 비밀번호가 틀린 경우의 예외 메시지가 같다")
    @Test
    void loginFailureMessagesAreIdentical() {
        Member member = savedMember(1L);
        given(memberRepository.findByEmail("none@example.com")).willReturn(Optional.empty());
        given(memberRepository.findByEmail(EMAIL)).willReturn(Optional.of(member));
        given(passwordEncoder.matches("wrong-password", ENCODED_PASSWORD)).willReturn(false);

        Throwable unknownEmail = catchThrowable(() -> memberService.login("none@example.com", PASSWORD));
        Throwable wrongPassword = catchThrowable(() -> memberService.login(EMAIL, "wrong-password"));

        assertThat(unknownEmail).isInstanceOf(InvalidCredentialsException.class);
        assertThat(wrongPassword).isInstanceOf(InvalidCredentialsException.class);
        assertThat(unknownEmail).hasMessage(wrongPassword.getMessage());
    }

    private Member savedMember(Long id) {
        given(passwordEncoder.encode(PASSWORD)).willReturn(ENCODED_PASSWORD);
        Member member = Member.of(EMAIL, PASSWORD, passwordEncoder);
        ReflectionTestUtils.setField(member, "id", id);
        return member;
    }
}
