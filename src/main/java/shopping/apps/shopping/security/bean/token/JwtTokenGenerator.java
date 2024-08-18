package shopping.apps.shopping.security.bean.token;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.JWTVerifier;
import lombok.NonNull;
import shopping.domains.user.core.domain.entity.TokenGenerator;

import java.time.Instant;
import java.util.Date;
import java.util.UUID;

public class JwtTokenGenerator implements TokenGenerator {
    private final String secret;

    private final Long expire;

    public JwtTokenGenerator(
            final String secret,
            final Long expire
    ) {
        this.secret = secret;
        this.expire = expire;
    }

    @Override
    public @NonNull String createToken(@NonNull final UUID uuid) {
        return JWT.create()
                .withClaim(KEY, uuid.toString())
                .withExpiresAt(new Date(System.currentTimeMillis() + expire))
                .sign(Algorithm.HMAC512(secret));
    }

    @Override
    public @NonNull boolean verifyToken(@NonNull final String token) {
        try {
            final JWTVerifier verifier = JWT.require(Algorithm.HMAC512(secret)).build();

            verifier.verify(token);

            return true;
        } catch (JWTVerificationException exception) {
            return exception.getMessage().contains("expired");
        }
    }

    @Override
    public boolean verifyTokenNotExpired(@NonNull final String token) {
        try {
            final JWTVerifier verifier = JWT.require(Algorithm.HMAC512(secret)).build();
            return verifier.verify(token).getExpiresAt().compareTo(Date.from(Instant.now())) > 0;
        } catch (JWTVerificationException exception) {
            return false;
        }
    }

    @Override
    public @NonNull UUID getUuidFromToken(@NonNull final String token) {
        JWTVerifier verifier = JWT.require(Algorithm.HMAC512(secret)).build();
        return UUID.fromString(verifier.verify(token).getClaim(KEY).asString());
    }
}
