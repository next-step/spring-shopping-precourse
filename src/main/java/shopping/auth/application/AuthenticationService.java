package shopping.auth.application;

import java.time.Clock;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shopping.auth.domain.AccessToken;
import shopping.auth.persistence.TokenRepository;
import shopping.common.error.ErrorCode;
import shopping.common.error.ShoppingException;

@Service
public class AuthenticationService {

    private final TokenRepository tokens;
    private final Clock clock;

    public AuthenticationService(TokenRepository tokens, Clock clock) {
        this.tokens = tokens;
        this.clock = clock;
    }

    @Transactional(readOnly = true)
    public Long authenticate(String value) {
        AccessToken token = tokens.findById(value)
                .orElseThrow(() -> new ShoppingException(ErrorCode.INVALID_TOKEN));

        return token.authenticateAt(clock.instant());
    }
}
