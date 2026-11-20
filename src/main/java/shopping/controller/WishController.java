package shopping.controller;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import shopping.domain.Member;
import shopping.domain.Product;
import shopping.domain.ProductId;
import shopping.service.WishService;

// 위시 리스트의 wishId는 담은 상품의 ID와 같다.
@RestController
@RequestMapping("/api/wishes")
public class WishController {
    private final WishService service;

    public WishController(WishService service) {
        this.service = service;
    }

    // 인증(LoginMember)이 요청 본문을 읽는 것보다 먼저 처리되도록 회원 파라미터를 앞에 둔다.
    @PostMapping
    public ResponseEntity<WishResponse> add(@LoginMember Member member,
            @RequestBody WishRequest request) {
        Product product = service.add(member, new ProductId(request.productIdValue()));
        return ResponseEntity.status(HttpStatus.CREATED).body(WishResponse.from(product));
    }

    @GetMapping
    public List<WishResponse> list(@LoginMember Member member) {
        return service.list(member).stream().map(WishResponse::from).toList();
    }

    @DeleteMapping("/{wishId}")
    public ResponseEntity<Void> delete(@LoginMember Member member, @PathVariable long wishId) {
        service.remove(member, new ProductId(wishId));
        return ResponseEntity.noContent().build();
    }
}
