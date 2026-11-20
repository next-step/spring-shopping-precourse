package shopping.controller;

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

import shopping.domain.Wish;
import shopping.service.WishService;

@RestController
@RequestMapping("/api/wishes")
public class WishController {

    private final WishService wishService;

    public WishController(WishService wishService) {
        this.wishService = wishService;
    }

    @PostMapping
    public ResponseEntity<Wish> addWish(@RequestHeader("Authorization") String token,
                                        @RequestBody WishRequest request) {
        Wish wish = wishService.addWish(token, request.productId());

        return ResponseEntity.ok(wish);
    }

    @GetMapping
    public ResponseEntity<List<Wish>> findWishes(@RequestHeader("Authorization") String token) {
        List<Wish> wishes = wishService.findWishes(token);

        return ResponseEntity.ok(wishes);
    }

    @DeleteMapping("/{wishId}")
    public ResponseEntity<Void> deleteWish(@RequestHeader("Authorization") String token,
                                           @PathVariable Long wishId) {
        wishService.deleteWish(token, wishId);

        return ResponseEntity.ok().build();
    }

    record WishRequest(Long productId) {
    }
}
