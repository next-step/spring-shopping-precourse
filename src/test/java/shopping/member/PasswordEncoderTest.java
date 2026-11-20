package shopping.member;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class PasswordEncoderTest {
    private final PasswordEncoder passwordEncoder = new PasswordEncoder();

    @Test
    void 같은_비밀번호는_같은_값으로_암호화된다() {
        assertThat(passwordEncoder.encode("password123")).isEqualTo(passwordEncoder.encode("password123"));
    }

    @Test
    void 비밀번호는_원문과_다른_값으로_저장된다() {
        assertThat(passwordEncoder.encode("password123")).isNotEqualTo("password123");
    }
}
