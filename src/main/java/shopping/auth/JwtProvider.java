package shopping.auth;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import shopping.exception.UnauthorizedException;

@Component
public class JwtProvider {
    private static final String BEARER_PREFIX = "Bearer ";
    private final SecretKey key;
    private final long expirationMs;

    public JwtProvider(@Value("${jwt.secret}") String secret,
                       @Value("${jwt.expiration-ms}") long expirationMs) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expirationMs = expirationMs;
    }

    public String createToken(Long memberId) {
        Date now = new Date();
        return Jwts.builder()
                .subject(String.valueOf(memberId))
                .issuedAt(now)
                .expiration(new Date(now.getTime() + expirationMs))
                .signWith(key)
                .compact();
    }
    public Long getMemberIdFromHeader(String authorization) {
        if (authorization == null || !authorization.startsWith(BEARER_PREFIX)) {
            throw new UnauthorizedException("인증 토큰이 필요합니다.");
        }
        return getMemberId(authorization.substring(BEARER_PREFIX.length()));
    }
    public Long getMemberId(String token) {
        try {
            String subject = Jwts.parser().verifyWith(key).build()
                    .parseSignedClaims(token).getPayload().getSubject();
            return Long.valueOf(subject);
        } catch (JwtException | IllegalArgumentException e) {
            throw new UnauthorizedException("유효하지 않은 토큰입니다.");
        }
    }
}