package shopping.member;

import java.util.regex.Pattern;

public class Member {
    private static final Pattern EMAIL = Pattern.compile("^[\\w.+-]+@[\\w-]+(\\.[\\w-]+)+$");

    private final Long id;
    private final String email;
    private final String password;

    public Member(Long id, String email, String password) {
        validateEmail(email);
        validatePassword(password);
        this.id = id;
        this.email = email;
        this.password = password;
    }

    public Member withId(Long newId) {
        return new Member(newId, email, password);
    }

    public boolean matchesPassword(String rawPassword) {
        return password.equals(rawPassword);
    }

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    private static void validateEmail(String email) {
        if (email == null || !EMAIL.matcher(email).matches()) {
            throw new IllegalArgumentException("올바른 이메일 형식이 아닙니다.");
        }
    }

    private static void validatePassword(String password) {
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("비밀번호는 비어 있을 수 없습니다.");
        }
    }
}