package shopping.product;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class PurgoMalumProfanityCheckerTest {

    private final PurgoMalumProfanityChecker checker = new PurgoMalumProfanityChecker();

    @Test
    @DisplayName("비속어가 있으면 true를 돌려준다")
    void findsProfanity() {
        assertThat(checker.contains("fuck")).isTrue();
    }

    @Test
    @DisplayName("비속어가 없으면 false를 돌려준다")
    void passesCleanWord() {
        assertThat(checker.contains("strawberry")).isFalse();
    }
}