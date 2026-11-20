package shopping.wish;

import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import shopping.auth.Authenticator;
import shopping.member.Member;

@RestController
@RequestMapping("/api/wishes")
public class WishController {
    private final WishService service;
    private final Authenticator authenticator;

    public WishController(WishService service, Authenticator authenticator) {
        this.service = service;
        this.authenticator = authenticator;
    }

    @PostMapping
    public ResponseEntity<WishResponse> add(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @RequestBody WishRequest request) {
        Member member = authenticator.authenticate(authorization);
        WishResponse response = service.add(member.getId(), request.productId());
        return ResponseEntity.created(URI.create("/api/wishes/" + response.id())).body(response);
    }

    @GetMapping
    public List<WishResponse> getAll(
            @RequestHeader(value = "Authorization", required = false) String authorization) {
        Member member = authenticator.authenticate(authorization);
        return service.getAll(member.getId());
    }

    @DeleteMapping("/{wishId}")
    public ResponseEntity<Void> remove(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @PathVariable("wishId") Long wishId) {
        Member member = authenticator.authenticate(authorization);
        service.remove(member.getId(), wishId);
        return ResponseEntity.noContent().build();
    }
}