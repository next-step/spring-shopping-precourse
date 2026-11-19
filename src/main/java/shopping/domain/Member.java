package shopping.domain;

import java.util.Objects;

public class Member {
    private final Email email;
    private final Password password;
    private final WishList wishList;

    public Member(Email email, Password password) {
        this.email = Objects.requireNonNull(email, "이메일은 null일 수 없습니다.");
        this.password = Objects.requireNonNull(password, "비밀번호는 null일 수 없습니다.");
        this.wishList = new WishList();
    }

    public Email email() {
        return email;
    }

    public Password password() {
        return password;
    }

    public WishList wishList() {
        return wishList;
    }
}
