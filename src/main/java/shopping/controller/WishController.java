package shopping.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import shopping.dto.AddWishRequest;
import shopping.service.MemberService;
import shopping.domain.Wish;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@RestController
@RequestMapping("/api/wishes")
public class WishController {
    private final MemberService memberService;
    private final ProductController productController;
    private final Map<String, Map<Long, Wish>> wishesByEmail = new ConcurrentHashMap<>();
    private final AtomicLong nextWishId = new AtomicLong(1);

    public WishController(MemberService memberService, ProductController productController) {
        this.memberService = memberService;
        this.productController = productController;
    }

    @GetMapping
    public List<Wish> getWishes(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization) {
        String email = memberService.findEmailByAuthorization(authorization);
        return List.copyOf(wishesByEmail.getOrDefault(email, Map.of()).values());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Wish addWish(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization,
            @Valid @RequestBody AddWishRequest request) {
        String email = memberService.findEmailByAuthorization(authorization);
        Long productId = request.productId();
        productController.getProduct(productId);
        Map<Long, Wish> wishes = wishesByEmail.computeIfAbsent(email, key -> new ConcurrentHashMap<>());
        Wish wish = new Wish(nextWishId.getAndIncrement(), productId);
        if (wishes.putIfAbsent(productId, wish) != null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 추가한 상품입니다.");
        }
        return wish;
    }

    @DeleteMapping("/{wishId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteWish(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization,
            @PathVariable("wishId") long wishId) {
        String email = memberService.findEmailByAuthorization(authorization);
        Map<Long, Wish> wishes = wishesByEmail.get(email);

        if (wishes == null || !wishes.values().removeIf(wish -> wish.wishId() == wishId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "위시리스트 항목을 찾을 수 없습니다.");
        }
    }
}
