package shopping.auth;

import org.springframework.stereotype.Component;
import shopping.auth.exception.InvalidTokenException;

@Component
public class AuthenticatedMemberResolver {

    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtTokenProvider jwtTokenProvider;

    public AuthenticatedMemberResolver(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    public Long resolve(String authorization) {
        return jwtTokenProvider.getMemberId(extractToken(authorization));
    }

    private String extractToken(String authorization) {
        if (authorization == null || !authorization.startsWith(BEARER_PREFIX)) {
            throw new InvalidTokenException();
        }
        String token = authorization.substring(BEARER_PREFIX.length());
        if (token.isBlank()) {
            throw new InvalidTokenException();
        }
        return token;
    }
}
