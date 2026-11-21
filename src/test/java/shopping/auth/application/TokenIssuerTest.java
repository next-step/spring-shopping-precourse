package shopping.auth.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import shopping.auth.domain.AccessToken;
import shopping.auth.persistence.TokenRepository;

@DisplayName("인증 토큰 발급")
class TokenIssuerTest {

    @DisplayName("생성한 토큰을 요청한 회원에 연결해 저장한다")
    @Test
    void persistsGeneratedTokenForRequestedMember() {
        TokenRepository repository = mock(TokenRepository.class);
        Clock clock = Clock.fixed(Instant.EPOCH, ZoneOffset.UTC);
        when(repository.save(any(AccessToken.class))).thenAnswer(call -> call.getArgument(0));
        TokenIssuer issuer = new TokenIssuer(repository, () -> "generated", clock);

        AccessToken issued = issuer.issue(7L);

        verify(repository).save(issued);
        assertThat(issued.value()).isEqualTo("generated");
        assertThat(issued.authenticateAt(clock.instant())).isEqualTo(7L);
    }
}
