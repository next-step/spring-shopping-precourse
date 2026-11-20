package shopping.product.service;

import java.util.List;
import java.util.NoSuchElementException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shopping.product.dto.AddProductRequestDto;
import shopping.product.dto.ProductListResponseDto;
import shopping.product.dto.ProductResponseDto;
import shopping.product.dto.UpdateProductRequestDto;
import shopping.product.entity.Product;
import shopping.product.repository.ProductRepository;
import shopping.product.util.ProductNameValidator;
import shopping.product.util.ProfanityChecker;

@Service
@Transactional(readOnly = true)
public class ProductService {
    private final ProductRepository productRepository;
    private final ProfanityChecker profanityChecker;

    public ProductService(ProductRepository productRepository, ProfanityChecker profanityChecker) {
        this.productRepository = productRepository;
        this.profanityChecker = profanityChecker;
    }

    public ProductResponseDto getProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("상품이 존재하지 않습니다. id=" + id));
        return ProductResponseDto.from(product);
    }

    public ProductListResponseDto getProductList() {
        List<ProductResponseDto> products = productRepository.findAll().stream()
                .map(product -> ProductResponseDto.from(product))
                .toList();
        return new ProductListResponseDto(products);
    }

    @Transactional
    public ProductResponseDto addProduct(AddProductRequestDto request) {
        ProductNameValidator.validate(request.name());
        profanityChecker.validate(request.name());
        Product product = new Product(request.name(), request.price(), request.count(), request.url());
        Product savedProduct = productRepository.save(product);
        return ProductResponseDto.from(savedProduct);
    }

    @Transactional
    public void updateProduct(Long id, UpdateProductRequestDto request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("상품이 존재하지 않습니다. id=" + id));
        ProductNameValidator.validate(request.name());
        profanityChecker.validate(request.name());
        String name = request.name();
        Integer price = request.price();
        Integer count = request.count();
        String url = request.url();
        product.update(name, price, count, url);

        productRepository.save(product);
    }

    @Transactional
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("상품이 존재하지 않습니다. id=" + id));
        productRepository.delete(product);
    }
}
