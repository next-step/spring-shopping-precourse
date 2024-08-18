package shopping.domains.product.core.in.adapter;

import lombok.NonNull;
import shopping.domains.product.core.in.command.CreateProductCommand;
import shopping.domains.product.core.domain.dto.ProductDto;
import shopping.domains.product.core.in.command.UpdateProductCommand;

import java.util.List;
import java.util.UUID;

public interface ProductInAdapter {
    @NonNull
    ProductDto createProduct(@NonNull final CreateProductCommand command);

    @NonNull
    ProductDto updateProduct(@NonNull final UpdateProductCommand command);

    void deleteProduct(@NonNull final UUID productId);

    @NonNull
    ProductDto getProduct(@NonNull final UUID productId);

    @NonNull
    List<ProductDto> getAllProduct();
}
