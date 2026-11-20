package shopping.service;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import shopping.dto.MemberRequest;
import shopping.dto.MemberResponse;
import shopping.model.Member;
import shopping.repository.MemberRepository;

@Service
public class MemberService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public MemberService(MemberRepository memberRepository, PasswordEncoder passwordEncoder) {
        this.memberRepository = memberRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public MemberResponse register(MemberRequest request) {
        validateDuplicateEmail(request.email());
        Member member = new Member(
                request.email(), passwordEncoder.encode(request.password()));
        return new MemberResponse(memberRepository.save(member).getId());
    }

    public MemberResponse login(MemberRequest request) {
        Member member = memberRepository.findByEmail(request.email())
                .orElseThrow(this::invalidCredentials);
        if (!passwordEncoder.matches(request.password(), member.getPasswordHash())) {
            throw invalidCredentials();
        }
        return new MemberResponse(member.getId());
    }

    private ResponseStatusException invalidCredentials() {
        return new ResponseStatusException(
                HttpStatus.UNAUTHORIZED, "이메일 또는 비밀번호가 일치하지 않습니다");
    }

    private void validateDuplicateEmail(String email) {
        if (memberRepository.existsByEmail(email)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 등록된 이메일입니다");
        }
    }
}
