package shopping.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BCryptPasswordEncoderAdapterTests {

    private final BCryptPasswordEncoderAdapter passwordEncoder = new BCryptPasswordEncoderAdapter();

    @DisplayName("인코딩한 값은 원문과 다르고 원문과 일치한다")
    @Test
    void encodeAndMatches() {
        String encoded = passwordEncoder.encode("password123");

        assertThat(encoded).isNotEqualTo("password123");
        assertThat(passwordEncoder.matches("password123", encoded)).isTrue();
    }

    @DisplayName("다른 비밀번호는 일치하지 않는다")
    @Test
    void doesNotMatch() {
        String encoded = passwordEncoder.encode("password123");

        assertThat(passwordEncoder.matches("password124", encoded)).isFalse();
    }
}
