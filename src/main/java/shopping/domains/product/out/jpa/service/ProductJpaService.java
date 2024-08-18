package shopping.domains.product.out.jpa.service;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import shopping.domains.product.core.domain.dto.ProductDto;
import shopping.domains.product.core.out.adapter.ProductOutAdapter;
import shopping.domains.product.out.jpa.entity.JpaProduct;
import shopping.domains.product.out.jpa.repository.ProductRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductJpaService implements ProductOutAdapter {
    private final ProductRepository productRepository;

    @Override
    public @NonNull ProductDto save(@NonNull final ProductDto dto) {
        return productRepository.save(new JpaProduct(dto)).toDto();
    }

    @Override
    public @NonNull Optional<ProductDto> findProduct(@NonNull final UUID productId) {
        return productRepository.findById(productId).map(JpaProduct::toDto);
    }

    @Override
    public @NonNull List<ProductDto> findAll() {
        return productRepository.findAll()
                .stream()
                .map(JpaProduct::toDto)
                .toList();
    }

    @Override
    public void delete(@NonNull final UUID productId) {
        productRepository.deleteById(productId);
    }
}
