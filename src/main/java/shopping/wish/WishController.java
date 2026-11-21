package shopping.wish;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import shopping.auth.LoginMember;

@RestController
@RequestMapping("/api/wishes")
public class WishController {

    private final WishService wishService;

    public WishController(WishService wishService) {
        this.wishService = wishService;
    }

    @PostMapping
    public ResponseEntity<WishResponse> add(@LoginMember Long memberId,
            @RequestBody WishRequest request) {
        WishResponse response = wishService.add(memberId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<WishResponse>> findAll(@LoginMember Long memberId) {
        return ResponseEntity.ok(wishService.findAll(memberId));
    }

    @DeleteMapping("/{wishId}")
    public ResponseEntity<Void> delete(@LoginMember Long memberId, @PathVariable Long wishId) {
        wishService.delete(memberId, wishId);
        return ResponseEntity.noContent().build();
    }
}
