package shopping;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MemberAuthenticatorTest {

    @Mock private TokenService tokenService;

    @InjectMocks private MemberAuthenticator authenticator;

    @ParameterizedTest
    @ValueSource(
            strings = {
                "Bearer valid-token_123",
                "bearer valid-token_123",
                "BEARER valid-token_123",
                "Bearer   valid-token_123"
            })
    void extractsTokenAndReturnsAuthenticatedMember(String header) {
        given(tokenService.authenticate("valid-token_123")).willReturn(1L);
        assertThat(authenticator.authenticate(header)).isEqualTo(1L);
        verify(tokenService).authenticate("valid-token_123");
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(
            strings = {
                "   ",
                "Bearer",
                "Bearer ",
                "Basic abc",
                "Bearer abc def",
                "Bearer abc,def",
                "Bearer abc!",
                "Bearer abc\n",
                " Bearer abc"
            })
    void rejectsMalformedHeaderBeforeTokenLookup(String header) {
        assertThatThrownBy(() -> authenticator.authenticate(header))
                .isInstanceOf(InvalidTokenException.class);
        verifyNoInteractions(tokenService);
    }

    @Test
    void propagatesUnknownOrExpiredTokenFailure() {
        given(tokenService.authenticate("invalid-token")).willThrow(new InvalidTokenException());
        assertThatThrownBy(() -> authenticator.authenticate("Bearer invalid-token"))
                .isInstanceOf(InvalidTokenException.class);
    }
}
