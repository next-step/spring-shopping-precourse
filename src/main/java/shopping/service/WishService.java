package shopping.service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import shopping.domain.Member;
import shopping.domain.Product;
import shopping.domain.ProductId;

public class WishService {
    private final ProductService productService;

    public WishService(ProductService productService) {
        validateNotNull(productService, "상품 서비스가 설정되지 않았습니다.");
        this.productService = productService;
    }

    // 존재하는 상품만 담을 수 있다. 없는 상품이면 NoSuchElementException이 올라온다.
    public Product add(Member member, ProductId productId) {
        validateNotNull(member, "회원 정보가 없습니다.");
        Product product = productService.get(productId);
        member.addWish(product);
        return product;
    }

    // 담은 뒤 삭제된 상품은 보이지 않게 한다.
    public List<Product> list(Member member) {
        validateNotNull(member, "회원 정보가 없습니다.");
        Set<ProductId> existing = existingProductIds();
        return member.wishes().stream()
                .filter(product -> existing.contains(product.id().orElseThrow()))
                .toList();
    }

    public void remove(Member member, ProductId productId) {
        validateNotNull(member, "회원 정보가 없습니다.");
        member.removeWish(productId);
    }

    private Set<ProductId> existingProductIds() {
        return productService.list().stream()
                .map(product -> product.id().orElseThrow())
                .collect(Collectors.toSet());
    }

    private static void validateNotNull(Object value, String message) {
        if (value == null) {
            throw new NullPointerException(message);
        }
    }
}
