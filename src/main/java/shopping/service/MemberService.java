package shopping.service;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import shopping.dto.MemberRequest;
import shopping.dto.MemberResponse;
import shopping.dto.TokenResponse;
import shopping.model.Email;
import shopping.model.Member;
import shopping.model.Password;
import shopping.model.PasswordHash;
import shopping.repository.MemberRepository;

@Service
public class MemberService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    public MemberService(
            MemberRepository memberRepository,
            PasswordEncoder passwordEncoder,
            TokenService tokenService) {
        this.memberRepository = memberRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenService = tokenService;
    }

    public MemberResponse register(MemberRequest request) {
        Email email = new Email(request.email());
        Password password = new Password(request.password());
        validateDuplicateEmail(email);
        Member member = createMember(email, password);
        return new MemberResponse(memberRepository.save(member).getId());
    }

    public TokenResponse login(MemberRequest request) {
        Email email = new Email(request.email());
        Password password = new Password(request.password());
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(this::invalidCredentials);
        validatePassword(password, member);
        return tokenService.issue(member.getId());
    }

    private Member createMember(Email email, Password password) {
        String encoded = passwordEncoder.encode(password.value());
        return new Member(email, new PasswordHash(encoded));
    }

    private void validatePassword(Password password, Member member) {
        if (!passwordEncoder.matches(password.value(), member.getPasswordHash().value())) {
            throw invalidCredentials();
        }
    }

    private ResponseStatusException invalidCredentials() {
        return new ResponseStatusException(
                HttpStatus.UNAUTHORIZED,
                "이메일 또는 비밀번호가 일치하지 않습니다");
    }

    private void validateDuplicateEmail(Email email) {
        if (memberRepository.existsByEmail(email)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "이미 등록된 이메일입니다");
        }
    }
}
