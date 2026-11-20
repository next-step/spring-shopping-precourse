package shopping.member.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import shopping.member.domain.Member;
import shopping.member.service.MemberService;

@RestController
@RequestMapping("/api/members")
public class MemberController {
    private final MemberService memberService;

    public MemberController (MemberService memberService) {
        this.memberService = memberService;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public MemberResponse register(@Valid @RequestBody MemberRequest request) {
        Member member = memberService.register(request.email(), request.password());
        return MemberResponse.from(member);
    }

    @PostMapping("/login")
    public TokenResponse login(@Valid @RequestBody MemberRequest request) {
        String token = memberService.login(request.email(), request.password());
        return new TokenResponse(token);
    }
}
