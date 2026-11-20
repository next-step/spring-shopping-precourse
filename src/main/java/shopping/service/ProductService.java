package shopping.service;

import java.util.List;
import java.util.NoSuchElementException;
import shopping.domain.Product;
import shopping.domain.ProductId;
import shopping.domain.ProductImage;
import shopping.domain.ProductName;
import shopping.domain.ProductPrice;
import shopping.domain.Products;
import shopping.domain.ProfanityChecker;

public class ProductService {
    private final Products products;
    private final ProfanityChecker profanityChecker;
    private long lastId = 0;

    public ProductService(Products products, ProfanityChecker profanityChecker) {
        validateNotNull(products, "상품 목록이 설정되지 않았습니다.");
        validateNotNull(profanityChecker, "비속어 검사기가 설정되지 않았습니다.");
        this.products = products;
        this.profanityChecker = profanityChecker;
    }

    // ID 부여와 추가를 한 덩어리로 실행하고, 추가에 성공했을 때만 ID를 소비한다.
    public synchronized Product create(String name, long price, String imageUrl) {
        ProductName productName = new ProductName(name, profanityChecker);
        ProductPrice productPrice = new ProductPrice(price);
        ProductImage image = new ProductImage(imageUrl);
        Product product = new Product(new ProductId(lastId + 1), productName, productPrice, image);
        products.add(product);
        lastId++;
        return product;
    }

    public Product get(ProductId id) {
        return products.findById(id).orElseThrow(
                () -> new NoSuchElementException("존재하지 않는 상품입니다. (상품 번호: " + id.value() + ")"));
    }

    public List<Product> list() {
        return products.all();
    }

    public synchronized Product update(ProductId id, String name, long price, String imageUrl) {
        ProductName productName = new ProductName(name, profanityChecker);
        ProductPrice productPrice = new ProductPrice(price);
        ProductImage image = new ProductImage(imageUrl);
        products.update(id, productName, productPrice, image);
        return get(id);
    }

    public synchronized void delete(ProductId id) {
        products.remove(id);
    }

    private static void validateNotNull(Object value, String message) {
        if (value == null) {
            throw new NullPointerException(message);
        }
    }
}
