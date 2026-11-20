package shopping.domain;

public record Member(Long id, String email, String password) {

    public Member {
        if (email == null || !email.contains("@")) {
            throw new IllegalArgumentException("유효한 이메일 형식이 아닙니다.");
        }
        if (password == null || password.length() < 4) {
            throw new IllegalArgumentException("비밀번호는 4자리 이상이어야 합니다.");
        }
    }

    public boolean hasEmail(String email) {
        return this.email.equals(email);
    }

    public boolean hasPassword(String password) {
        return this.password.equals(password);
    }
}
