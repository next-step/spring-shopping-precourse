package shopping.wish.web;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import shopping.auth.web.CurrentMemberResolver;
import shopping.wish.application.WishService;

@RestController
@RequestMapping("/api/wishes")
public class WishController {

    private final WishService wishes;
    private final CurrentMemberResolver currentMemberResolver;

    public WishController(WishService wishes, CurrentMemberResolver currentMemberResolver) {
        this.wishes = wishes;
        this.currentMemberResolver = currentMemberResolver;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public WishResponse add(@RequestHeader(value = "Authorization", required = false) String header,
            @Valid @RequestBody WishRequest request,
            @RequestParam Map<String, String> parameters) {
        Long memberId = currentMemberResolver.resolve(header);

        if (!parameters.isEmpty()) {
            throw new IllegalArgumentException("위시 추가에는 쿼리 파라미터를 사용하지 않습니다.");
        }

        return WishResponse.of(wishes.add(memberId, request.productId()));
    }

    @GetMapping
    public List<WishResponse> list(
            @RequestHeader(value = "Authorization", required = false) String header,
            @RequestParam Map<String, String> parameters) {
        Long memberId = currentMemberResolver.resolve(header);

        if (!parameters.isEmpty()) {
            throw new IllegalArgumentException("위시 목록 조회에는 쿼리 파라미터를 사용하지 않습니다.");
        }

        return wishes.list(memberId).stream().map(WishResponse::of).toList();
    }

    @DeleteMapping("/{wishId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@RequestHeader(value = "Authorization", required = false) String header,
            @PathVariable Long wishId) {
        Long memberId = currentMemberResolver.resolve(header);
        wishes.delete(memberId, wishId);
    }
}
