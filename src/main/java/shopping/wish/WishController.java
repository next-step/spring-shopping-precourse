package shopping.wish;

import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import shopping.auth.JwtProvider;
import shopping.wish.dto.WishRequest;
import shopping.wish.dto.WishResponse;

@RestController
@RequestMapping("/api/wishes")
public class WishController {

    private final WishService wishService;
    private final JwtProvider jwtProvider;

    public WishController(WishService wishService, JwtProvider jwtProvider) {
        this.wishService = wishService;
        this.jwtProvider = jwtProvider;
    }

    @PostMapping
    public ResponseEntity<WishResponse> add(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization,
            @Valid @RequestBody WishRequest request) {
        Long memberId = jwtProvider.getMemberIdFromHeader(authorization);
        WishResponse response = wishService.add(memberId, request.productId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<WishResponse>> findAll(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization) {
        Long memberId = jwtProvider.getMemberIdFromHeader(authorization);
        return ResponseEntity.ok(wishService.findAll(memberId));
    }

    @DeleteMapping("/{wishId}")
    public ResponseEntity<Void> delete(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization,
            @PathVariable Long wishId) {
        Long memberId = jwtProvider.getMemberIdFromHeader(authorization);
        wishService.delete(memberId, wishId);
        return ResponseEntity.noContent().build();
    }
}