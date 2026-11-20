package shopping.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import shopping.service.MemberService;

@RestController
@RequestMapping("/api/members")
public class MemberController {

    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @PostMapping("/register")
    public ResponseEntity<TokenResponse> register(@RequestBody MemberRequest request) {
        String token = memberService.register(request.email(), request.password());

        return ResponseEntity.ok(new TokenResponse(token));
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@RequestBody MemberRequest request) {
        String token = memberService.login(request.email(), request.password());

        return ResponseEntity.ok(new TokenResponse(token));
    }

    record MemberRequest(String email, String password) {
    }

    record TokenResponse(String token) {
    }
}
