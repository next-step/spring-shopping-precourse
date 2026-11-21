package shopping.auth;

import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shopping.common.UnauthorizedException;

@Service
public class AuthTokenService {

    private static final String BEARER_PREFIX = "Bearer ";
    private static final String INVALID_TOKEN_MESSAGE = "인증 토큰이 없거나 유효하지 않습니다.";

    private final AuthTokenRepository authTokenRepository;

    public AuthTokenService(AuthTokenRepository authTokenRepository) {
        this.authTokenRepository = authTokenRepository;
    }

    @Transactional
    public String issue(Long memberId) {
        String token = UUID.randomUUID().toString();
        authTokenRepository.save(new AuthToken(token, memberId));
        return token;
    }

    @Transactional(readOnly = true)
    public Long findMemberId(String authorization) {
        String token = extractToken(authorization);
        return authTokenRepository.findByToken(token)
                .map(AuthToken::getMemberId)
                .orElseThrow(() -> new UnauthorizedException(INVALID_TOKEN_MESSAGE));
    }

    private String extractToken(String authorization) {
        if (authorization == null || !authorization.startsWith(BEARER_PREFIX)) {
            throw new UnauthorizedException(INVALID_TOKEN_MESSAGE);
        }
        return authorization.substring(BEARER_PREFIX.length());
    }
}
