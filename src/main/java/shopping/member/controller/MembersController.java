package shopping.member.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import shopping.member.dto.LoginRequestDto;
import shopping.member.dto.LoginResponseDto;
import shopping.member.dto.MemberSignUpRequestDto;
import shopping.member.dto.MemberSignupResponseDto;
import shopping.member.service.MemberService;

@RestController
@RequestMapping("/api/members")
@CrossOrigin
public class MembersController {
    private final MemberService memberService;

    public MembersController(MemberService memberService) {
        this.memberService = memberService;
    }

    @PostMapping("/register")
    public ResponseEntity<MemberSignupResponseDto> signup(@Valid @RequestBody MemberSignUpRequestDto request) {
        MemberSignupResponseDto response = memberService.signup(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@Valid @RequestBody LoginRequestDto request) {
        LoginResponseDto response = memberService.login(request);
        return ResponseEntity.ok(response);
    }
}
