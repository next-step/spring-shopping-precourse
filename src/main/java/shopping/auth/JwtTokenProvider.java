package shopping.auth;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import shopping.common.exception.UnauthorizedException;

@Component
public class JwtTokenProvider implements TokenProvider {

    private final SecretKey key;
    private final long expirationMillis;

    public JwtTokenProvider(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration-millis}") long expirationMillis) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expirationMillis = expirationMillis;
    }

    @Override
    public String createToken(Long memberId) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + expirationMillis);
        return Jwts.builder()
                .subject(String.valueOf(memberId))
                .issuedAt(now)
                .expiration(expiration)
                .signWith(key)
                .compact();
    }

    @Override
    public Long extractMemberId(String token) {
        try {
            return Long.valueOf(parseSubject(token));
        } catch (JwtException | IllegalArgumentException e) {
            throw new UnauthorizedException("유효하지 않은 토큰입니다.");
        }
    }

    private String parseSubject(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }
}
