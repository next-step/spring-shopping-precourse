package shopping.wish;

import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import shopping.member.LoginMember;
import shopping.member.Member;

@RestController
@RequestMapping("/api/wishes")
public class WishController {

    private final WishService wishService;

    public WishController(WishService wishService) {
        this.wishService = wishService;
    }

    @PostMapping
    public ResponseEntity<WishResponse> add(@LoginMember Member member, @RequestBody WishRequest request) {
        WishResponse response = wishService.add(member.getId(), request);
        return ResponseEntity.created(URI.create("/api/wishes/" + response.id()))
                .body(response);
    }

    @GetMapping
    public ResponseEntity<List<WishResponse>> findAll(@LoginMember Member member) {
        return ResponseEntity.ok(wishService.findAll(member.getId()));
    }

    @DeleteMapping("/{wishId}")
    public ResponseEntity<Void> remove(@LoginMember Member member, @PathVariable("wishId") Long wishId) {
        wishService.remove(member.getId(), wishId);
        return ResponseEntity.noContent().build();
    }
}
