package shopping.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import shopping.dto.MemberRequest;
import shopping.dto.TokenResponse;
import shopping.service.AuthToken;
import shopping.service.MemberService;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/register")
    public ResponseEntity<TokenResponse> register(@RequestBody @Valid MemberRequest request) {
        AuthToken token = memberService.register(request.email(), request.password());

        return ResponseEntity.status(HttpStatus.CREATED).body(TokenResponse.from(token));
    }

    @PostMapping("/login")
    public TokenResponse login(@RequestBody @Valid MemberRequest request) {
        AuthToken token = memberService.login(request.email(), request.password());

        return TokenResponse.from(token);
    }
}
