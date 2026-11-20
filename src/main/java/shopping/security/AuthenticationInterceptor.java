package shopping.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import shopping.exception.InvalidTokenException;
import shopping.service.JwtProvider;

@Component
@RequiredArgsConstructor
public class AuthenticationInterceptor implements HandlerInterceptor {

    public static final String MEMBER_ID_ATTRIBUTE = "memberId";
    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtProvider jwtProvider;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String token = extractToken(request.getHeader(HttpHeaders.AUTHORIZATION));
        request.setAttribute(MEMBER_ID_ATTRIBUTE, jwtProvider.getMemberId(token));

        return true;
    }

    private String extractToken(String header) {
        if (header == null || !header.startsWith(BEARER_PREFIX)) {
            throw new InvalidTokenException("Authorization header must be a Bearer token");
        }

        String token = header.substring(BEARER_PREFIX.length()).trim();
        if (token.isEmpty()) {
            throw new InvalidTokenException("Bearer token is empty");
        }

        return token;
    }
}
