package shopping.repository;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import shopping.model.AuthToken;

class TokenRepositoryTest {
    private TokenRepository tokenRepository;

    @BeforeEach
    void setUp() {
        tokenRepository = new TokenRepository();
    }

    @Test
    @DisplayName("토큰을 저장하면 문자열로 회원 ID를 조회할 수 있다")
    void saveAndFindToken() {
        tokenRepository.save(new AuthToken("token", 1L));

        assertThat(tokenRepository.findByValue("token"))
                .get()
                .extracting(AuthToken::memberId)
                .isEqualTo(1L);
    }
}
