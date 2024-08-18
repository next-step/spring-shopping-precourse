package shopping.apps.shopping.api.product.response;

import lombok.NonNull;
import shopping.domains.product.core.domain.dto.ProductDto;

import java.util.Collection;
import java.util.List;

public record ProductInfoResponses(
        @NonNull List<ProductInfoResponse> data
) {
    public ProductInfoResponses(@NonNull final Collection<ProductDto> productDto) {
        this(productDto.stream().map(ProductInfoResponse::new).toList());
    }
}
