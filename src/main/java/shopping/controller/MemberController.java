package shopping.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import shopping.dto.MemberRequest;
import shopping.service.MemberService;
import shopping.dto.TokenResponse;

@RestController
@RequestMapping("/api/members")
public class MemberController {
    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public TokenResponse register(@Valid @RequestBody MemberRequest request) {
        return memberService.register(request);
    }

    @PostMapping("/login")
    public TokenResponse login(@Valid @RequestBody MemberRequest request) {
        return memberService.login(request);
    }
}

