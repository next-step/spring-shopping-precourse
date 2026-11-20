package shopping.security;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import shopping.exception.InvalidTokenException;
import shopping.service.JwtProvider;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JjwtProvider implements JwtProvider {

    private final SecretKey secretKey;
    private final long expirationMillis;

    public JjwtProvider(@Value("${jwt.secret}") String secret,
                        @Value("${jwt.expiration}") long expirationMillis) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expirationMillis = expirationMillis;
    }

    @Override
    public String createToken(Long memberId) {
        Date now = new Date();

        return Jwts.builder()
                .subject(String.valueOf(memberId))
                .issuedAt(now)
                .expiration(new Date(now.getTime() + expirationMillis))
                .signWith(secretKey)
                .compact();
    }

    @Override
    public long getExpiresInSeconds() {
        return expirationMillis / 1000;
    }

    @Override
    public Long getMemberId(String token) {
        try {
            String subject = Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload()
                    .getSubject();

            return Long.valueOf(subject);
        } catch (JwtException | IllegalArgumentException e) {
            throw new InvalidTokenException("Invalid token", e);
        }
    }
}
