package shopping;

import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/wishes")
public class WishController {

    private final WishService wishService;

    public WishController(WishService wishService) {
        this.wishService = wishService;
    }

    @PostMapping
    public ResponseEntity<WishResponse> add(
            @RequestAttribute(MemberAuthenticationInterceptor.MEMBER_ID) Long memberId,
            @Valid @RequestBody WishCreateRequest request) {
        WishResponse response = wishService.add(memberId, request.productId());
        return ResponseEntity.created(URI.create("/api/wishes/" + response.id())).body(response);
    }

    @GetMapping
    public List<WishResponse> findAll(
            @RequestAttribute(MemberAuthenticationInterceptor.MEMBER_ID) Long memberId) {
        return wishService.findAll(memberId);
    }

    @DeleteMapping("/{wishId}")
    public ResponseEntity<Void> delete(
            @RequestAttribute(MemberAuthenticationInterceptor.MEMBER_ID) Long memberId,
            @PathVariable("wishId") Long wishId) {
        wishService.delete(memberId, wishId);
        return ResponseEntity.noContent().build();
    }
}
