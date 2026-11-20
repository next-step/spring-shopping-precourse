package shopping;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.Instant;

@Entity
public class MemberToken {

    @Id
    @Column(length = 64)
    private String tokenHash;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(nullable = false)
    private Instant expiresAt;

    protected MemberToken() {}

    public MemberToken(String tokenHash, Member member, Instant expiresAt) {
        this.tokenHash = tokenHash;
        this.member = member;
        this.expiresAt = expiresAt;
    }

    public Long getMemberId() {
        return member.getId();
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }
}
