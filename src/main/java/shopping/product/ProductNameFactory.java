package shopping.product;

import org.springframework.stereotype.Component;

@Component
public class ProductNameFactory {
    private final ProfanityChecker profanityChecker;

    public ProductNameFactory(ProfanityChecker profanityChecker) {
        this.profanityChecker = profanityChecker;
    }

    public ProductName create(String rawName) {
        ProductName name = new ProductName(rawName);
        validateNoProfanity(name);
        return name;
    }

    private void validateNoProfanity(ProductName name) {
        if (profanityChecker.containsProfanity(name.getValue())) {
            throw new IllegalArgumentException("상품 이름에 비속어가 포함될 수 없습니다.");
        }
    }
}