package shopping.product.application;

import shopping.product.domain.ProductName;

public interface ProfanityChecker {
    boolean containsProfanity(ProductName name);
}
