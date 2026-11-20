package shopping.model;

public class Member {
    private Long id;
    private final String email;
    private final String passwordHash;

    public Member(String email, String passwordHash) {
        this.email = email;
        this.passwordHash = passwordHash;
    }

    public Long getId() {
        return id;
    }

    public void assignId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }
}
