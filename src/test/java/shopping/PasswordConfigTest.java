package shopping;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

class PasswordConfigTest {

    private final PasswordEncoder passwordEncoder = new PasswordConfig().passwordEncoder();

    @Test
    void encodesPasswordAndMatchesOriginal() {
        String rawPassword = "password123!";
        String encodedPassword = passwordEncoder.encode(rawPassword);

        assertThat(encodedPassword).isNotEqualTo(rawPassword);
        assertThat(passwordEncoder.matches(rawPassword, encodedPassword)).isTrue();
    }

    @Test
    void rejectsWrongPassword() {
        String encodedPassword = passwordEncoder.encode("password123!");

        assertThat(passwordEncoder.matches("wrong-password", encodedPassword)).isFalse();
    }

    @Test
    void generatesDifferentHashesForSamePassword() {
        String first = passwordEncoder.encode("password123!");
        String second = passwordEncoder.encode("password123!");

        assertThat(first).isNotEqualTo(second);
        assertThat(passwordEncoder.matches("password123!", first)).isTrue();
        assertThat(passwordEncoder.matches("password123!", second)).isTrue();
    }
}
