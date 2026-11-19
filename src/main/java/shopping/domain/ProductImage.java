package shopping.domain;

import java.net.URI;
import java.net.URISyntaxException;

public class ProductImage {
    private static final String ALLOWED_SCHEME = "https";

    private final String url;

    public ProductImage(String url) {
        validateNotBlank(url);
        URI uri = parse(url);
        validateHttps(uri);
        validateHost(uri);
        this.url = url;
    }

    private void validateNotBlank(String url) {
        if (url == null || url.isBlank()) {
            throw new IllegalArgumentException("상품 이미지 URL은 비어 있을 수 없습니다.");
        }
    }

    private void validateHttps(URI uri) {
        if (!ALLOWED_SCHEME.equalsIgnoreCase(uri.getScheme())) {
            throw new IllegalArgumentException("상품 이미지 URL은 https만 사용할 수 있습니다.");
        }
    }

    private void validateHost(URI uri) {
        if (uri.getHost() == null) {
            throw new IllegalArgumentException("상품 이미지 URL에 호스트가 없습니다.");
        }
    }

    private URI parse(String url) {
        try {
            return new URI(url);
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("상품 이미지 URL 형식이 올바르지 않습니다.", e);
        }
    }

    public String url() {
        return url;
    }
}
