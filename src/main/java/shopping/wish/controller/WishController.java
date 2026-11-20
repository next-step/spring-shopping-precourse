package shopping.wish.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import shopping.auth.LoginMemberId;
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
    @ResponseStatus(HttpStatus.CREATED)
    public WishResponse add(@LoginMemberId Long memberId,
                            @Valid @RequestBody WishRequest request) {
        return wishService.add(memberId, request);
    }

    @DeleteMapping("/{wishId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@LoginMemberId Long memberId, @PathVariable Long wishId) {
        wishService.delete(memberId, wishId);
    }

    @GetMapping
    public List<WishResponse> findAll(@LoginMemberId Long memberId) {
        return wishService.findAll(memberId);
    }
}
