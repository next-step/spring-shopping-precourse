package shopping.model;

public class Member {
    private Long id;
    private final Email email;
    private final PasswordHash passwordHash;

    public Member(Email email, PasswordHash passwordHash) {
        if (email == null || passwordHash == null) {
            throw new IllegalArgumentException("회원 정보는 null일 수 없습니다");
        }
        this.email = email;
        this.passwordHash = passwordHash;
    }

    public Long getId() {
        return id;
    }

    public void assignId(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("회원 ID는 null일 수 없습니다");
        }
        this.id = id;
    }

    public Email getEmail() {
        return email;
    }

    public PasswordHash getPasswordHash() {
        return passwordHash;
    }
}
