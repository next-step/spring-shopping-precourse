package shopping.wish.controller;

import java.util.List;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import shopping.auth.LoginMember;
import shopping.wish.domain.Wish;
import shopping.wish.service.WishService;

@RestController
@RequestMapping("/api/wishes")
public class WishController {
    private final WishService wishService;

    public WishController(WishService wishService) {
        this.wishService = wishService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public WishResponse add(
            @LoginMember Long memberId,
            @Valid @RequestBody WishRequest request) {
        Wish wish = wishService.add(memberId, request.productId());
        return WishResponse.from(wish);
    }

    @GetMapping
    public List<WishResponse> findAll(@LoginMember Long memberId) {
        return wishService.findAll(memberId).stream()
                .map(WishResponse::from)
                .toList();
    }

    @DeleteMapping("/{wishId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@LoginMember Long memberId, @PathVariable Long wishId) {
        wishService.delete(memberId, wishId);
    }
}
