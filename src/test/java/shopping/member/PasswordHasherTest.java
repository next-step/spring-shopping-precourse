package shopping.member;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class PasswordHasherTest {

    private final PasswordHasher passwordHasher = new PasswordHasher();

    @Test
    void 같은_비밀번호는_같은_해시가_된다() {
        assertThat(passwordHasher.hash("password1")).isEqualTo(passwordHasher.hash("password1"));
    }

    @Test
    void 해시는_원문과_다르고_다른_비밀번호와도_다르다() {
        String hashed = passwordHasher.hash("password1");

        assertThat(hashed).isNotEqualTo("password1");
        assertThat(hashed).isNotEqualTo(passwordHasher.hash("password2"));
    }
}
