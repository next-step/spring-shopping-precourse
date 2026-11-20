package shopping.domain;

import java.util.List;
import java.util.Optional;

public class Member {
    private final MemberId id;
    private final Email email;
    private final Password password;
    private final WishList wishList;

    public Member(Email email, Password password) {
        validateNotNull(email, "이메일 정보가 없습니다.");
        validateNotNull(password, "비밀번호 정보가 없습니다.");
        this.id = null;
        this.email = email;
        this.password = password;
        this.wishList = new WishList();
    }

    public Member(MemberId id, Email email, Password password) {
        validateNotNull(id, "회원 번호가 없습니다.");
        validateNotNull(email, "이메일 정보가 없습니다.");
        validateNotNull(password, "비밀번호 정보가 없습니다.");
        this.id = id;
        this.email = email;
        this.password = password;
        this.wishList = new WishList();
    }

    public Optional<MemberId> id() {
        return Optional.ofNullable(id);
    }

    public Email email() {
        return email;
    }

    public Password password() {
        return password;
    }

    public boolean hasEmail(Email other) {
        validateNotNull(other, "비교할 이메일이 없습니다.");
        return email.equals(other);
    }

    public boolean matchesPassword(Password given) {
        return password.matches(given);
    }

    public void addWish(Product product) {
        wishList.add(product);
    }

    public void removeWish(ProductId productId) {
        wishList.remove(productId);
    }

    public List<Product> wishes() {
        return wishList.all();
    }

    private static void validateNotNull(Object value, String message) {
        if (value == null) {
            throw new NullPointerException(message);
        }
    }
}
