package shopping.service;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import shopping.domain.MemberId;

public class TokenStore {
    private final Map<String, MemberId> tokens = new ConcurrentHashMap<>();

    public String issue(MemberId memberId) {
        validateNotNull(memberId, "회원 번호가 없습니다.");
        String token = UUID.randomUUID().toString();
        tokens.put(token, memberId);
        return token;
    }

    public Optional<MemberId> find(String token) {
        if (token == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(tokens.get(token));
    }

    private static void validateNotNull(Object value, String message) {
        if (value == null) {
            throw new NullPointerException(message);
        }
    }
}
