package shopping.domains.user.core.domain.entity;

import lombok.NonNull;

public interface HashStrategy {
    @NonNull
    String encrypt(@NonNull final String origin);

    boolean match(
            @NonNull final String origin,
            @NonNull final String encrypted
    );
}
