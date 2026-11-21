package shopping.product;

import org.springframework.context.annotation.Primary;

@Primary
public class FakeProfanityChecker implements ProfanityChecker {

    public static final String PROFANITY = "badword";

    @Override
    public boolean containsProfanity(String text) {
        return text.contains(PROFANITY);
    }
}
