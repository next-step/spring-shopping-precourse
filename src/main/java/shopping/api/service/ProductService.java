package shopping.api.service;

import org.springframework.stereotype.Service;
import shopping.api.entity.CreateProductRequest;
import shopping.domain.entity.Product;
import shopping.domain.util.IDGenarator;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class ProductService {

    private final Map<Long, Product> database = new HashMap<>();

    public Product save(CreateProductRequest createProductRequest) {
        long id = IDGenarator.generate();

        Product product = new Product(id, createProductRequest.getName(), createProductRequest.getPrice(), createProductRequest.getImageUrl());
        database.put(id, product);

        return product;
    }

    public Product findById(Long id) throws RuntimeException{
        if(database.isEmpty())
            throw new RuntimeException("등록된 상품이 없습니다.");

        return database.get(id);
    }
}
