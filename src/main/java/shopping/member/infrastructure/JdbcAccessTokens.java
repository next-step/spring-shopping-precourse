package shopping.member.infrastructure;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.HexFormat;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Component;
import shopping.member.application.AccessTokens;

@Component
public final class JdbcAccessTokens implements AccessTokens {
    private final JdbcClient jdbc;
    private final Duration lifetime;
    private final SecureRandom random = new SecureRandom();

    public JdbcAccessTokens(JdbcClient jdbc, @Value("${shopping.auth.token-ttl:PT24H}") Duration ttl) {
        if (ttl.isNegative() || ttl.isZero()) {
            throw new IllegalArgumentException("Token lifetime must be positive");
        }
        this.jdbc = jdbc;
        this.lifetime = ttl;
    }

    @Override
    public String issue(long memberId) {
        byte[] bytes = new byte[32];
        random.nextBytes(bytes);
        String token = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
        jdbc.sql("""
                INSERT INTO access_tokens (token_hash, member_id, expires_at)
                VALUES (:hash, :memberId, :expiresAt)
                """)
                .param("hash", hash(token)).param("memberId", memberId)
                .param("expiresAt", Instant.now().plus(lifetime).toEpochMilli())
                .update();
        return token;
    }

    @Override
    public Optional<Long> findMemberId(String token) {
        return jdbc.sql("""
                SELECT member_id FROM access_tokens
                WHERE token_hash = :hash AND expires_at > :now
                """)
                .param("hash", hash(token)).param("now", Instant.now().toEpochMilli())
                .query(Long.class).optional();
    }

    private static String hash(String token) {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256")
                    .digest(token.getBytes(StandardCharsets.US_ASCII));
            return HexFormat.of().formatHex(digest);
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 is unavailable", exception);
        }
    }
}
