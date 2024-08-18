package shopping.domains.product.core.domain.service;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import shopping.domains.common.core.domain.entity.ClientIllegalArgumentException;
import shopping.domains.common.core.domain.entity.ResourceNotFoundException;
import shopping.domains.common.core.domain.enums.CommonErrorCode;
import shopping.domains.product.core.in.command.CreateProductCommand;
import shopping.domains.product.core.domain.dto.ProductDto;
import shopping.domains.product.core.domain.entity.Image;
import shopping.domains.product.core.domain.entity.Name;
import shopping.domains.product.core.domain.entity.Price;
import shopping.domains.product.core.domain.entity.Product;
import shopping.domains.product.core.in.adapter.ProductInAdapter;
import shopping.domains.product.core.in.command.UpdateProductCommand;
import shopping.domains.product.core.out.adapter.ProductOutAdapter;
import shopping.domains.product.core.out.adapter.ProfanityFilterOutAdapter;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService implements ProductInAdapter {
    private final ProductOutAdapter productOutAdapter;

    private final ProfanityFilterOutAdapter profanityFilterOutAdapter;

    @Override
    public @NonNull ProductDto createProduct(@NonNull final CreateProductCommand command) {
        final Product product = Product.builder()
                .name(getCleanName(command.name()))
                .price(new Price(command.price()))
                .image(new Image(command.imageUrl()))
                .build();
        return productOutAdapter.save(product.toDto());
    }

    @Override
    public @NonNull ProductDto updateProduct(@NonNull final UpdateProductCommand command) {
        final ProductDto productDto = productOutAdapter.findProduct(command.productId())
                .orElseThrow(() -> new ResourceNotFoundException(CommonErrorCode.NOT_EXIST))
                .toBuilder()
                .name(getCleanName(command.name()).getValue())
                .price(command.price())
                .imageUrl(command.imageUrl())
                .build();
        final ProductDto updateProductDto = new Product(productDto).toDto().updateBase(productDto);

        return productOutAdapter.save(updateProductDto);
    }

    private Name getCleanName(@NonNull final String name) {
        final Name cleanName = new Name(name);

        if (profanityFilterOutAdapter.isNotCleanText(cleanName.getValue())) {
            throw new ClientIllegalArgumentException(CommonErrorCode.BAD_REQUEST);
        }

        return cleanName;
    }


    @Override
    public void deleteProduct(@NonNull final UUID productId) {
        productOutAdapter.delete(productId);
    }

    @Override
    public @NonNull ProductDto getProduct(@NonNull final UUID productId) {
        return productOutAdapter.findProduct(productId).orElseThrow(() -> new ResourceNotFoundException(CommonErrorCode.NOT_EXIST));
    }

    @Override
    public @NonNull List<ProductDto> getAllProduct() {
        return productOutAdapter.findAll();
    }
}
