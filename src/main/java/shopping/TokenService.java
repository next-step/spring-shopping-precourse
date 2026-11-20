package shopping;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Clock;
import java.time.Duration;
import java.util.Base64;
import java.util.HexFormat;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TokenService {

    private static final Duration TOKEN_LIFETIME = Duration.ofHours(24);
    private final MemberTokenRepository repository;
    private final Clock clock;
    private final SecureRandom random = new SecureRandom();

    public TokenService(MemberTokenRepository repository, Clock clock) {
        this.repository = repository;
        this.clock = clock;
    }

    @Transactional
    public AuthTokenResponse issue(Member member) {
        byte[] bytes = new byte[32];
        random.nextBytes(bytes);
        String token = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
        repository.save(
                new MemberToken(hashToken(token), member, clock.instant().plus(TOKEN_LIFETIME)));
        return new AuthTokenResponse(token, "Bearer", TOKEN_LIFETIME.toSeconds());
    }

    static String hashToken(String token) {
        try {
            byte[] digest =
                    MessageDigest.getInstance("SHA-256")
                            .digest(token.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(digest);
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256을 사용할 수 없습니다.", exception);
        }
    }

    @Transactional(readOnly = true)
    public Long authenticate(String token) {
        if (token == null || token.isBlank()) {
            throw new InvalidTokenException();
        }
        MemberToken stored =
                repository.findById(hashToken(token)).orElseThrow(InvalidTokenException::new);
        if (!stored.getExpiresAt().isAfter(clock.instant())) {
            throw new InvalidTokenException();
        }
        return stored.getMemberId();
    }
}
