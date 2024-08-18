package shopping.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import shopping.model.Member;
import shopping.model.Product;
import shopping.service.MemberService;
import shopping.service.ProductService;
import shopping.service.WishService;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/wishes")
public class WishController {

    private final WishService wishService;

    private final ProductService productService;

    private final MemberService memberService;

    public WishController(WishService wishService, ProductService productService, MemberService memberService) {
        this.wishService = wishService;
        this.productService = productService;
        this.memberService = memberService;
    }

    // 위시 리스트에 상품 추가
    @PostMapping
    public ResponseEntity<Map<String, String>> addProductToWishList(
            String email, // 로그인된 사용자 이메일
            @RequestBody @Valid ProductWishRequest wishRequest) {

        Optional<Member> memberOpt = memberService.findByEmail(email);
        if (memberOpt.isEmpty()) {
            return new ResponseEntity<>(Map.of("message", "User not found"), HttpStatus.NOT_FOUND);
        }

        Optional<Product> productOpt = productService.getProductById(wishRequest.getProductId());
        if (productOpt.isEmpty()) {
            return new ResponseEntity<>(Map.of("message", "Product not found"), HttpStatus.NOT_FOUND);
        }

        wishService.addProductToWishList(memberOpt.get(), productOpt.get());
        return new ResponseEntity<>(Map.of("message", "Product added to wish list"), HttpStatus.OK);
    }

    // 위시 리스트에서 상품 삭제
    @DeleteMapping("/{productId}")
    public ResponseEntity<Map<String, String>> removeProductFromWishList(
            String email, // 로그인된 사용자 이메일
            @PathVariable Long productId) {

        Optional<Member> memberOpt = memberService.findByEmail(email);
        if (memberOpt.isEmpty()) {
            return new ResponseEntity<>(Map.of("message", "User not found"), HttpStatus.NOT_FOUND);
        }

        boolean removed = wishService.removeProductFromWishList(memberOpt.get(), productId);
        if (removed) {
            return new ResponseEntity<>(Map.of("message", "Product removed from wish list"), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(Map.of("message", "Product not found in wish list"), HttpStatus.NOT_FOUND);
        }
    }

    // 로그인된 사용자의 위시 리스트 조회
    @GetMapping
    public ResponseEntity<Iterable<Product>> getWishList(String email) {
        Optional<Member> memberOpt = memberService.findByEmail(email);
        if (memberOpt.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        Iterable<Product> wishList = wishService.getWishList(memberOpt.get());
        return new ResponseEntity<>(wishList, HttpStatus.OK);
    }
}