package shopping.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import shopping.dto.MemberRequest;
import shopping.dto.MemberResponse;
import shopping.dto.TokenResponse;
import shopping.service.MemberService;

@RestController
@RequestMapping("/api/members")
public class MemberController {
    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @PostMapping("/login")
    public TokenResponse login(@Valid @RequestBody MemberRequest request) {
        return memberService.login(request);
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public MemberResponse register(@Valid @RequestBody MemberRequest request) {
        return memberService.register(request);
    }
}
