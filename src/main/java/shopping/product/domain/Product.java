package shopping.product.domain;

import java.math.BigDecimal;
import java.net.URI;
import java.net.URISyntaxException;

public record Product(ProductName name, BigDecimal price, String imageUrl) {
    public Product {
        if (name == null) {
            throw new IllegalArgumentException("상품 이름을 입력해 주세요.");
        }
        if (price == null || price.signum() <= 0) {
            throw new IllegalArgumentException("상품 가격은 0보다 커야 합니다.");
        }
        validateImageUrl(imageUrl);
    }

    private static void validateImageUrl(String imageUrl) {
        if (imageUrl == null || imageUrl.isBlank()) {
            throw new IllegalArgumentException("상품 이미지 URL을 입력해 주세요.");
        }
        URI uri = parseImageUrl(imageUrl);
        String scheme = uri.getScheme();
        boolean isHttp = "http".equalsIgnoreCase(scheme) || "https".equalsIgnoreCase(scheme);
        if (!isHttp || uri.getHost() == null) {
            throw new IllegalArgumentException("유효한 HTTP 또는 HTTPS 이미지 URL을 입력해 주세요.");
        }
    }

    private static URI parseImageUrl(String imageUrl) {
        try {
            return new URI(imageUrl);
        } catch (URISyntaxException exception) {
            throw new IllegalArgumentException("유효한 이미지 URL을 입력해 주세요.", exception);
        }
    }
}
