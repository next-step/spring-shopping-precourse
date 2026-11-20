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
import shopping.member.Member;
import shopping.wish.dto.WishRequest;
import shopping.wish.dto.WishResponse;

@RestController
@RequestMapping("/api/wishes")
public class WishController {

    private final WishService wishService;

    public WishController(WishService wishService) {
        this.wishService = wishService;
    }

    @PostMapping
    public ResponseEntity<WishResponse> add(@LoginMember Member member,
                                            @RequestBody WishRequest request) {
        WishResponse response = wishService.add(member, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<WishResponse>> findAll(@LoginMember Member member) {
        return ResponseEntity.ok(wishService.findAll(member));
    }

    @DeleteMapping("/{wishId}")
    public ResponseEntity<Void> delete(@LoginMember Member member, @PathVariable Long wishId) {
        wishService.delete(member, wishId);
        return ResponseEntity.noContent().build();
    }
}