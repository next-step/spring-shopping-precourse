package shopping.repository;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Repository;
import shopping.model.AuthToken;

@Repository
public class TokenRepository {
    private final Map<String, AuthToken> tokens = new ConcurrentHashMap<>();

    public void save(AuthToken token) {
        tokens.put(token.value(), token);
    }

    public Optional<AuthToken> findByValue(String value) {
        return Optional.ofNullable(tokens.get(value));
    }
}
