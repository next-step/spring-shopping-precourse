package shopping.auth.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import shopping.auth.domain.AccessToken;
import shopping.auth.persistence.TokenRepository;
import shopping.common.error.ErrorCode;
import shopping.common.error.ShoppingException;

@DisplayName("토큰 인증 서비스")
class AuthenticationServiceTest {

    private final TokenRepository tokens = mock(TokenRepository.class);
    private final Clock clock = Clock.fixed(Instant.EPOCH.plusSeconds(3600), ZoneOffset.UTC);
    private final AuthenticationService service = new AuthenticationService(tokens, clock);

    @DisplayName("저장된 토큰에 연결된 회원을 식별한다")
    @Test
    void identifiesMemberFromStoredToken() {
        when(tokens.findById("valid"))
                .thenReturn(Optional.of(AccessToken.issue("valid", 7L, clock.instant())));

        assertThat(service.authenticate("valid")).isEqualTo(7L);
    }

    @DisplayName("등록되지 않은 토큰은 인증에 실패한다")
    @Test
    void missingTokenIsRejected() {
        when(tokens.findById("missing")).thenReturn(Optional.empty());

        assertInvalid("missing");
    }

    @DisplayName("현재 시각을 기준으로 만료된 토큰을 거절한다")
    @Test
    void checksExpirationUsingCurrentTime() {
        when(tokens.findById("expired"))
                .thenReturn(Optional.of(AccessToken.issue("expired", 7L, Instant.EPOCH)));

        assertInvalid("expired");
    }

    private void assertInvalid(String value) {
        assertThatThrownBy(() -> service.authenticate(value))
                .isInstanceOfSatisfying(ShoppingException.class,
                        error -> assertThat(error.code()).isEqualTo(ErrorCode.INVALID_TOKEN));
    }
}
