package shopping.product.domain;

@FunctionalInterface
public interface ProfanityChecker {

    boolean containsProfanity(String text);
}
