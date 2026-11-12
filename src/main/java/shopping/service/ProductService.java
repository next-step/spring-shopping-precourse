package shopping.service;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import shopping.model.Product;
import shopping.repository.ProductRepository;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {
    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    // 상품 생성
    public Product createProduct(Product product) {
        // setId 호출 불필요 - JPA가 id를 자동으로 생성
        return productRepository.save(product); // 저장 시 JPA가 id를 자동으로 생성
    }

    // 상품 수정
    public Product updateProduct(Long productId, Product productDetails) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        // 상품 필드 업데이트
        product.setName(productDetails.getName());
        product.setPrice(productDetails.getPrice());
        product.setImageUrl(productDetails.getImageUrl());

        return productRepository.save(product); // 기존 상품 업데이트
    }

    // 상품 조회
    public Optional<Product> getProductById(Long productId) {
        return productRepository.findById(productId);
    }

    // 상품 삭제
    public void deleteProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));
        productRepository.delete(product);
    }

    public Iterable<Product> getAllProducts() {
        return productRepository.findAll();
    }
}
