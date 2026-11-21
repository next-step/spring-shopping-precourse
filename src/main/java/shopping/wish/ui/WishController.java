package shopping.wish.ui;

import jakarta.validation.Valid;
import java.util.List;
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
import shopping.auth.AuthenticatedMemberResolver;
import shopping.wish.application.WishService;
import shopping.wish.dto.WishRequest;
import shopping.wish.dto.WishResponse;

@RestController
@RequestMapping("/api/wishes")
public class WishController {

    private static final String AUTHORIZATION = "Authorization";

    private final WishService wishService;
    private final AuthenticatedMemberResolver memberResolver;

    public WishController(WishService wishService, AuthenticatedMemberResolver memberResolver) {
        this.wishService = wishService;
        this.memberResolver = memberResolver;
    }

    @PostMapping
    public ResponseEntity<WishResponse> add(
            @RequestHeader(value = AUTHORIZATION, required = false) String authorization,
            @Valid @RequestBody WishRequest request) {
        Long memberId = memberResolver.resolve(authorization);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(wishService.add(memberId, request.productId()));
    }

    @GetMapping
    public ResponseEntity<List<WishResponse>> findAll(
            @RequestHeader(value = AUTHORIZATION, required = false) String authorization) {
        Long memberId = memberResolver.resolve(authorization);
        return ResponseEntity.ok(wishService.findAll(memberId));
    }

    @DeleteMapping("/{wishId}")
    public ResponseEntity<Void> delete(
            @RequestHeader(value = AUTHORIZATION, required = false) String authorization,
            @PathVariable Long wishId) {
        wishService.delete(memberResolver.resolve(authorization), wishId);
        return ResponseEntity.noContent().build();
    }
}
