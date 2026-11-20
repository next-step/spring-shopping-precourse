package shopping;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import shopping.product.ProfanityChecker;

@TestConfiguration
public class FakeProfanityCheckerConfig {

    public static final String PROFANITY = "badword";

    @Bean
    @Primary
    public ProfanityChecker fakeProfanityChecker() {
        return text -> text.contains(PROFANITY);
    }
}
