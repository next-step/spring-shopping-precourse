package shopping.controller;

public record MemberRequest(String email, String password) {
    public void requireFields() {
        if (email == null || password == null) {
            throw new IllegalArgumentException("이메일과 비밀번호를 입력해 주세요.");
        }
    }
}
