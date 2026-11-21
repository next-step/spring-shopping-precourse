package shopping.auth.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Duration;
import java.time.Instant;
import shopping.common.error.ErrorCode;
import shopping.common.error.ShoppingException;

@Entity
@Table(name = "access_tokens")
public class AccessToken {

    private static final Duration VALIDITY = Duration.ofHours(1);

    @Id
    @Column(name = "token_value", length = 64)
    private String value;
    @Column(nullable = false)
    private Long memberId;
    @Column(nullable = false)
    private Instant expiresAt;

    protected AccessToken() {
    }

    private AccessToken(String value, Long memberId, Instant expiresAt) {
        this.value = value;
        this.memberId = memberId;
        this.expiresAt = expiresAt;
    }

    public static AccessToken issue(String value, Long memberId, Instant issuedAt) {
        return new AccessToken(value, memberId, issuedAt.plus(VALIDITY));
    }

    public boolean isExpired(Instant now) {
        return !now.isBefore(expiresAt);
    }

    public Long authenticateAt(Instant now) {
        if (isExpired(now)) {
            throw new ShoppingException(ErrorCode.INVALID_TOKEN);
        }

        return memberId;
    }

    public String value() {
        return value;
    }
}
