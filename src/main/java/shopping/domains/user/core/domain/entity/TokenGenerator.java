package shopping.domains.user.core.domain.entity;

import lombok.NonNull;

import java.util.UUID;

public interface TokenGenerator {
    String KEY = "id";
    @NonNull
    String createToken(@NonNull final UUID uuid);

    @NonNull
    boolean verifyToken(@NonNull final String token);

    @NonNull
    boolean verifyTokenNotExpired(@NonNull final String token);

    @NonNull
    UUID getUuidFromToken(@NonNull final String token);
}
