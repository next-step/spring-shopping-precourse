package shopping.domain;

import java.net.URI;
import java.net.URISyntaxException;

public class ProductImage {
    private static final String ALLOWED_SCHEME = "https";
    private static final String INVALID_ADDRESS_MESSAGE =
            "상품 이미지 주소가 올바르지 않습니다. (예: https://example.com/image.png)";

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
            throw new IllegalArgumentException("상품 이미지 주소를 입력해 주세요.");
        }
    }

    private void validateHttps(URI uri) {
        if (!ALLOWED_SCHEME.equalsIgnoreCase(uri.getScheme())) {
            throw new IllegalArgumentException("상품 이미지 주소는 https://로 시작해야 합니다.");
        }
    }

    private void validateHost(URI uri) {
        if (uri.getHost() == null) {
            throw new IllegalArgumentException(INVALID_ADDRESS_MESSAGE);
        }
    }

    private URI parse(String url) {
        try {
            return new URI(url);
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException(INVALID_ADDRESS_MESSAGE, e);
        }
    }

    public String url() {
        return url;
    }
}
