package shopping.auth;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class AuthToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String token;

    private Long memberId;

    protected AuthToken() {
    }

    public AuthToken(String token, Long memberId) {
        this.token = token;
        this.memberId = memberId;
    }

    public Long getId() {
        return id;
    }

    public String getToken() {
        return token;
    }

    public Long getMemberId() {
        return memberId;
    }
}
