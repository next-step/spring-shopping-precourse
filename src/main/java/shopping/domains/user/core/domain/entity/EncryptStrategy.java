package shopping.domains.user.core.domain.entity;

import lombok.NonNull;

public interface EncryptStrategy {
    @NonNull
    String encrypt(@NonNull final String origin);

    @NonNull
    String decrypt(@NonNull final String encrypted);

    boolean match(
            @NonNull final String origin,
            @NonNull final String encrypted
    );
}
