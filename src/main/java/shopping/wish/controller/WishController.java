package shopping.wish.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import shopping.auth.LoginMember;
import shopping.wish.dto.WishRequest;
import shopping.wish.dto.WishResponse;
import shopping.wish.service.WishService;

import java.util.List;

@RestController
@RequestMapping("/api/wishes")
@RequiredArgsConstructor
public class WishController {

    private final WishService wishService;

    @PostMapping
    public ResponseEntity<WishResponse> add(@LoginMember Long memberId,
                                            @Valid @RequestBody WishRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(wishService.addWish(memberId, request.productId()));
    }

    @GetMapping
    public ResponseEntity<List<WishResponse>> getAll(@LoginMember Long memberId) {
        return ResponseEntity.ok(wishService.getWishes(memberId));
    }

    @DeleteMapping("/{wishId}")
    public ResponseEntity<Void> delete(@LoginMember Long memberId, @PathVariable Long wishId) {
        wishService.deleteWish(memberId, wishId);
        return ResponseEntity.ok().build();
    }
}
