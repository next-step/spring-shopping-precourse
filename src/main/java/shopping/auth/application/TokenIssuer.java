package shopping.auth.application;

import java.time.Clock;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shopping.auth.domain.AccessToken;
import shopping.auth.persistence.TokenRepository;

@Service
public class TokenIssuer {

    private final TokenRepository tokens;
    private final TokenGenerator generator;
    private final Clock clock;

    public TokenIssuer(TokenRepository tokens, TokenGenerator generator, Clock clock) {
        this.tokens = tokens;
        this.generator = generator;
        this.clock = clock;
    }

    @Transactional
    public AccessToken issue(Long memberId) {
        AccessToken token = AccessToken.issue(generator.generate(), memberId, clock.instant());
        return tokens.save(token);
    }
}
