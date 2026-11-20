package shopping.service;

import org.springframework.stereotype.Service;
import shopping.dto.TokenResponse;
import shopping.model.AuthToken;
import shopping.repository.TokenRepository;

@Service
public class TokenService {
    private final TokenGenerator tokenGenerator;
    private final TokenRepository tokenRepository;

    public TokenService(TokenGenerator tokenGenerator, TokenRepository tokenRepository) {
        this.tokenGenerator = tokenGenerator;
        this.tokenRepository = tokenRepository;
    }

    public TokenResponse issue(Long memberId) {
        String value = tokenGenerator.generate();
        tokenRepository.save(new AuthToken(value, memberId));
        return new TokenResponse(value);
    }
}
