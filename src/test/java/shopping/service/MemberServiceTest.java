package shopping.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import shopping.dto.MemberRequest;
import shopping.dto.TokenResponse;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.*;

class MemberServiceTest {
    private final MemberService service = new MemberService();
    private final MemberRequest member = new MemberRequest("reader@example.com", "sample-password");

    @Test
    void 가입한_회원이_로그인하면_토큰을_받는다() {
        service.register(member);

        TokenResponse response = service.login(member);

        assertThat(response.token()).isNotBlank();
        assertThat(service.findEmailByAuthorization("Bearer " + response.token()))
                .isEqualTo(member.email());
    }

    @Test
    void 이미_가입한_이메일로_가입을_시도하면_거절하고_기존_계정을_보존한다() {
        TokenResponse signup = service.register(member);
        MemberRequest duplicate = new MemberRequest(member.email(), "another-password");

        assertThatExceptionOfType(ResponseStatusException.class)
                .isThrownBy(() -> service.register(duplicate))
                .satisfies(error ->
                        assertThat(error.getStatusCode()).isEqualTo(HttpStatus.CONFLICT));
        assertThat(service.findEmailByAuthorization("Bearer " + signup.token()))
                .isEqualTo(member.email());
        assertThat(service.login(member).token()).isNotBlank();
    }

    @ParameterizedTest
    @CsvSource({"reader@example.com,wrong-password", "unknown@example.com,sample-password"})
    void 인증_정보가_일치하지_않으면_로그인을_거절한다(String email, String password) {
        service.register(member);
        MemberRequest input = new MemberRequest(email, password);

        assertThatExceptionOfType(ResponseStatusException.class)
                .isThrownBy(() -> service.login(input))
                .satisfies(error ->
                        assertThat(error.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED));
    }
}