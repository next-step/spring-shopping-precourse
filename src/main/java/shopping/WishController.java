package shopping;

import java.math.BigDecimal;
import java.util.List;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@Profile("local")
@RequestMapping("/api/wishes")
public class WishController {
    private final WishList wishes;

    public WishController(WishList wishes) {
        this.wishes = wishes;
    }

    @PostMapping
    public RegisteredWishResponse registeredWish(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization,
            @RequestBody WishRequest request) {
        Wish wish = wishes.registeredWish(token(authorization), productId(request));
        return new RegisteredWishResponse(wish.id(), wish.productId(), wish.quantity());
    }

    @GetMapping
    public List<WishResponse> items(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization) {
        return wishes.items(token(authorization)).stream().map(WishResponse::response).toList();
    }

    @ExceptionHandler(InvalidTokenException.class)
    ResponseEntity<Void> invalidToken() {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .header(HttpHeaders.WWW_AUTHENTICATE, "Bearer")
                .build();
    }

    @ExceptionHandler(ProductNotFoundException.class)
    ResponseEntity<Void> missingProduct() {
        return ResponseEntity.notFound().build();
    }

    private String token(String authorization) {
        String prefix = "Bearer ";
        if (authorization == null || !authorization.regionMatches(true, 0, prefix, 0, prefix.length())) {
            throw new InvalidTokenException();
        }
        return authorization.substring(prefix.length()).strip();
    }

    private long productId(WishRequest request) {
        if (request.productId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        try {
            return request.productId().longValueExact();
        } catch (ArithmeticException exception) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
    }

    public record WishRequest(BigDecimal productId) {
    }

    public record RegisteredWishResponse(Long wishId, Long productId, int quantity) {
    }

    public record WishResponse(Long wishId, ProductResponse product, int quantity) {
        static WishResponse response(WishItem item) {
            return new WishResponse(item.wishId(), ProductResponse.response(item.product()), item.quantity());
        }
    }
}
