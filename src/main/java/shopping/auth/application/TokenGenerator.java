package shopping.auth.application;

@FunctionalInterface
public interface TokenGenerator {

    String generate();
}
