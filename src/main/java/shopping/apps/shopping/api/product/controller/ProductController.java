package shopping.apps.shopping.api.product.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import shopping.apps.shopping.api.common.ApiUrls;
import shopping.apps.shopping.api.product.docs.ProductApiDocs;
import shopping.apps.shopping.api.product.request.WriteProductRequest;
import shopping.apps.shopping.api.product.response.ProductInfoResponse;
import shopping.apps.shopping.api.product.response.ProductInfoResponses;
import shopping.domains.product.core.domain.dto.ProductDto;
import shopping.domains.product.core.in.adapter.ProductInAdapter;

import java.util.List;
import java.util.UUID;

@Tag(name = "product-controller", description = "상품 컨트롤러")
@RestController
@RequestMapping(ApiUrls.Product.PREFIX)
@RequiredArgsConstructor
public class ProductController {
    private final ProductInAdapter productInAdapter;

    @Operation(summary = ProductApiDocs.Create.SUMMARY, description = ProductApiDocs.Create.DESCRIPTION)
    @PostMapping(ApiUrls.Product.CREATE)
    public ResponseEntity<ProductInfoResponse> createProduct(
            @Valid
            @RequestBody final WriteProductRequest request
    ) {
        final ProductDto productDto = productInAdapter.createProduct(request.toCreateCommand());
        return ResponseEntity.status(HttpStatus.CREATED).body(new ProductInfoResponse(productDto));
    }

    @Operation(summary = ProductApiDocs.Update.SUMMARY, description = ProductApiDocs.Update.DESCRIPTION)
    @PutMapping(ApiUrls.Product.Update.FULL_URI)
    public ResponseEntity<ProductInfoResponse> updateProduct(
            @PathVariable(ApiUrls.Product.Update.PRODUCT_ID_PATH_VAR) @NonNull final UUID productId,
            @Valid
            @RequestBody final WriteProductRequest request
    ) {
        final ProductDto productDto = productInAdapter.updateProduct(request.toUpdateCommand(productId));
        return ResponseEntity.status(HttpStatus.OK).body(new ProductInfoResponse(productDto));
    }

    @Operation(summary = ProductApiDocs.Delete.SUMMARY, description = ProductApiDocs.Delete.DESCRIPTION)
    @DeleteMapping(ApiUrls.Product.Delete.FULL_URI)
    public ResponseEntity<Void> deleteProduct(
            @PathVariable(ApiUrls.Product.Delete.PRODUCT_ID_PATH_VAR) @NonNull final UUID productId
    ) {
        productInAdapter.deleteProduct(productId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @Operation(summary = ProductApiDocs.GetInfo.SUMMARY, description = ProductApiDocs.GetInfo.DESCRIPTION)
    @GetMapping(ApiUrls.Product.GetInfo.FULL_URI)
    public ResponseEntity<ProductInfoResponse> getProductInfo(
            @PathVariable(ApiUrls.Product.GetInfo.PRODUCT_ID_PATH_VAR) @NonNull final UUID productId
    ) {
        final ProductDto productDto = productInAdapter.getProduct(productId);
        return ResponseEntity.status(HttpStatus.OK).body(new ProductInfoResponse(productDto));
    }

    @Operation(summary = ProductApiDocs.GetAllInfo.SUMMARY, description = ProductApiDocs.GetAllInfo.DESCRIPTION)
    @GetMapping(ApiUrls.Product.GET_ALL_INFO)
    public ResponseEntity<ProductInfoResponses> getAllProductInfo() {
        final List<ProductDto> productDtos = productInAdapter.getAllProduct();
        return ResponseEntity.status(HttpStatus.OK).body(new ProductInfoResponses(productDtos));
    }
}
