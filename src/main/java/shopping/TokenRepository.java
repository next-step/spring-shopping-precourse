package shopping;

import org.jspecify.annotations.Nullable;

public interface TokenRepository {
    String token(Long memberId);

    @Nullable Long memberId(String token);
}
