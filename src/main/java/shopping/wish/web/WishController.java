package shopping.wish.web;

import java.util.List;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import shopping.wish.application.WishRepository.ListedWish;
import shopping.wish.application.WishService;

@RestController
@RequestMapping(path = "/api/wishes", produces = MediaType.APPLICATION_JSON_VALUE)
public class WishController {
    private final WishService wishes;
    private final BearerAuthentication authentication;

    public WishController(WishService wishes, BearerAuthentication authentication) {
        this.wishes = wishes;
        this.authentication = authentication;
    }

    @GetMapping
    public List<ListedWish> list(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String header) {
        return wishes.list(authentication.requireMemberId(header));
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public AddedWish add(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String header,
            @RequestBody AddWish request) {
        long wishId = wishes.add(authentication.requireMemberId(header), request.productId());
        return new AddedWish(wishId, request.productId());
    }

    @DeleteMapping("/{wishId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String header,
            @PathVariable("wishId") long wishId) {
        wishes.delete(authentication.requireMemberId(header), wishId);
    }

    public record AddWish(Long productId) {}

    public record AddedWish(long wishId, long productId) {}
}
