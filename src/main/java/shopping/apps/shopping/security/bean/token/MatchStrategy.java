package shopping.apps.shopping.security.bean.token;

@FunctionalInterface
public interface MatchStrategy {
    boolean match(
            final String pattern,
            final String path
    );
}
