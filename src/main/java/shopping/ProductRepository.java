package shopping;

import java.util.List;
import org.jspecify.annotations.Nullable;

public interface ProductRepository {
    Product registeredProduct(String name, long price, String url);

    @Nullable Product product(Long id);

    List<Product> products();
}
