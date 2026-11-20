package shopping.client;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PurgoMalumClientTest {

    private final PurgoMalumClient client = new PurgoMalumClient();

    @Test
    void 비속어가_포함되면_true를_반환한다() {
        assertThat(client.containsProfanity("shit")).isTrue();
    }

    @Test
    void 비속어가_포함되지_않으면_false를_반환한다() {
        assertThat(client.containsProfanity("clean text")).isFalse();
    }
}
