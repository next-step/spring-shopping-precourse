package shopping.member.domain;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import shopping.common.error.ErrorCode;
import shopping.common.error.ShoppingException;

@Entity
@Table(name = "members")
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Embedded
    private Email email;
    @Embedded
    private Password password;

    protected Member() {
    }

    public Member(Email email, Password password) {
        if (email == null || password == null) {
            throw new IllegalArgumentException("이메일과 비밀번호는 필수입니다.");
        }

        this.email = email;
        this.password = password;
    }

    public void authenticate(Password candidate) {
        if (!password.matches(candidate)) {
            throw new ShoppingException(ErrorCode.INVALID_CREDENTIALS);
        }
    }

    public Long id() {
        return id;
    }

    public Email email() {
        return email;
    }
}
