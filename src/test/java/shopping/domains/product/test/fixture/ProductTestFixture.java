package shopping.domains.product.test.fixture;

import shopping.domains.product.core.domain.dto.ProductDto;
import shopping.domains.product.core.domain.entity.Image;
import shopping.domains.product.core.domain.entity.Name;
import shopping.domains.product.core.domain.entity.Price;
import shopping.domains.product.core.domain.entity.Product;
import shopping.domains.product.out.jpa.entity.JpaProduct;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class ProductTestFixture {
    public static final List<String> NAMES = List.of(
            "1",
            "최대글자는15글자입니다123",
            "()[]+-&/_는포함",
            "공백을 포함한 상품이름",
            "             양쪽 공백을 포함한 상품                ",
            "Hello World",
            "안녕하세요 123",
            "你好 世界",
            "123 456",
            "A1 + B2",
            "文字 と 数字"
    );

    public static final List<String> INVALID_NAMES = List.of(
            "!@#$%^*미포함",
            "최대글자인15글자를넘는프로덕트이름"
    );

    public static final List<String> IMAGE_URLS = List.of(
            "http://example.com/image.jpg",
            "https://example.com/image.jpeg",
            "http://example.com/path/to/image.png",
            "https://www.example.com/image.gif",
            "http://example.com/images/photo.bmp",
            "https://example.com/images/logo.svg",
            "           https://example.com/images/logo.svg        "
    );

    public static final List<String> INVALID_IMAGE_URLS = List.of(
            "ftp://example.com/image.jpg", // http, https가 아님
            "http://example.com/image.txt", // 이미지 파일 확장자가 아님
            "https://example.com/image", // 확장자 없음
            "http://example.com/image.jpg.extra", // 잘못된 확장자
            "http://example.com/imagejpg", // 점(.) 없음
            "example.com/image.jpg", // http, https로 시작하지 않음,
            "http://example.co  m/image.jpg" // 공백 존재
    );

    public static final List<Long> PRICES = List.of(
            0L,
            1L,
            9223372036854775807L
    );

    public static final List<Long> INVALID_PRICES = List.of(
            -1L
    );

    public static final Product PRODUCT = Product.builder()
            .name(new Name("name"))
            .price(new Price(0L))
            .image(new Image("https://example.com/image.jpg"))
            .build();

    public static final ProductDto PRODUCT_DTO = PRODUCT.toDto()
            .toBuilder()
            .id(UUID.randomUUID())
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .version(0L)
            .build();

    public static final JpaProduct JPA_PRODUCT = new JpaProduct(PRODUCT_DTO);
}
