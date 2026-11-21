package shopping;

import java.util.List;

public final class DefaultProductNameFilter implements ProductNameFilter {
    private final List<String> bannedWords;

    public DefaultProductNameFilter(List<String> bannedWords) {
        this.bannedWords = List.copyOf(bannedWords);
    }

    @Override
    public boolean profanity(String productName) {
        return bannedWords.stream().anyMatch(productName::contains);
    }
}
