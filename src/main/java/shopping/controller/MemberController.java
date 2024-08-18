package shopping.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import shopping.service.MemberService;

import java.util.Map;

@RestController
@RequestMapping("/api/members")
public class MemberController {

    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    // 회원 가입
    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> registerMember(
            @Valid @RequestBody MemberRegistrationRequest registrationRequest) {
        // 비밀번호는 평문으로 입력받음
        memberService.registerMember(registrationRequest.getEmail(), registrationRequest.getPassword());
        return new ResponseEntity<>(Map.of("message", "Member registered successfully"), HttpStatus.CREATED);
    }

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> loginMember(
            @Valid @RequestBody MemberLoginRequest loginRequest) {
        boolean authenticated = memberService.authenticateMember(loginRequest.getEmail(), loginRequest.getPassword());
        if (authenticated) {
            // 로그인 성공 시 토큰 발급 (간단히 메시지로 대체)
            return new ResponseEntity<>(Map.of("message", "Login successful"), HttpStatus.OK);
        } else {
            // 로그인 실패 시
            return new ResponseEntity<>(Map.of("message", "Invalid email or password"), HttpStatus.UNAUTHORIZED);
        }
    }
}
