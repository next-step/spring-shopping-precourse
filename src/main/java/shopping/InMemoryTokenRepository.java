package shopping;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.jspecify.annotations.Nullable;

public final class InMemoryTokenRepository implements TokenRepository {
    private final ConcurrentHashMap<String, Long> memberIds = new ConcurrentHashMap<>();

    @Override
    public String token(Long memberId) {
        String token;
        do {
            token = UUID.randomUUID().toString();
        } while (memberIds.putIfAbsent(token, memberId) != null);
        return token;
    }

    @Override
    public @Nullable Long memberId(String token) {
        return memberIds.get(token);
    }
}
