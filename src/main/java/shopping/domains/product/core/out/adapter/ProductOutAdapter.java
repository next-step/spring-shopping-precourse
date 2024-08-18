package shopping.domains.product.core.out.adapter;

import lombok.NonNull;
import shopping.domains.product.core.domain.dto.ProductDto;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductOutAdapter {
    @NonNull ProductDto save(@NonNull final ProductDto dto);

    @NonNull Optional<ProductDto> findProduct(@NonNull final UUID productId);

    @NonNull List<ProductDto> findAll();

    void delete(@NonNull final UUID productId);
}
