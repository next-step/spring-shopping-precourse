package shopping.domain;

@FunctionalInterface
public interface ProfanityChecker {
    boolean containsProfanity(String text);
}
