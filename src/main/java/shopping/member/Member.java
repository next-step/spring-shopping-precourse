package shopping.member;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.util.UUID;
import java.util.regex.Pattern;

@Entity
public class Member {
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;

    private String password;

    private String token;

    protected Member() {
    }

    public Member(String email, String encodedPassword) {
        validateEmail(email);
        this.email = email;
        this.password = encodedPassword;
    }

    private void validateEmail(String email) {
        if (email == null || !EMAIL_PATTERN.matcher(email).matches()) {
            throw new IllegalArgumentException("이메일 형식이 올바르지 않습니다.");
        }
    }

    public boolean matchesPassword(String encodedPassword) {
        return password.equals(encodedPassword);
    }

    public String issueToken() {
        this.token = UUID.randomUUID().toString();
        return token;
    }

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }
}
