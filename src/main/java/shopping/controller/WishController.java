package shopping.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import shopping.dto.WishRequest;
import shopping.dto.WishResponse;
import shopping.entity.Wish;
import shopping.security.AuthenticationInterceptor;
import shopping.service.WishService;

import java.util.List;

@RestController
@RequestMapping("/api/wishes")
@RequiredArgsConstructor
public class WishController {

    private final WishService wishService;

    @PostMapping
    public ResponseEntity<WishResponse> add(
            @RequestAttribute(AuthenticationInterceptor.MEMBER_ID_ATTRIBUTE) Long memberId,
            @RequestBody @Valid WishRequest request
    ) {
        Wish wish = wishService.add(memberId, request.productId());

        return ResponseEntity.status(HttpStatus.CREATED).body(WishResponse.from(wish));
    }

    @GetMapping
    public List<WishResponse> findAll(
            @RequestAttribute(AuthenticationInterceptor.MEMBER_ID_ATTRIBUTE) Long memberId
    ) {
        return wishService.findAll(memberId).stream()
                .map(WishResponse::from)
                .toList();
    }

    @DeleteMapping("/{wishId}")
    public ResponseEntity<Void> delete(
            @RequestAttribute(AuthenticationInterceptor.MEMBER_ID_ATTRIBUTE) Long memberId,
            @PathVariable Long wishId
    ) {
        wishService.delete(memberId, wishId);

        return ResponseEntity.noContent().build();
    }
}
