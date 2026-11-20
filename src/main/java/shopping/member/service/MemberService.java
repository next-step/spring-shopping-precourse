package shopping.member.service;

import io.jsonwebtoken.security.Password;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import shopping.auth.JwtTokenProvider;
import shopping.member.domain.Member;
import shopping.member.exception.DuplicateEmailException;
import shopping.member.exception.InvalidCredentialsException;
import shopping.member.repository.MemberRepository;

@Service
public class MemberService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;

    public MemberService(
            MemberRepository memberRepository,
            PasswordEncoder passwordEncoder,
            JwtTokenProvider tokenProvider) {
        this.memberRepository = memberRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
    }

    public Member register(String email, String password) {
        validateNotDuplicated(email);
        String encoded = passwordEncoder.encode(password);
        return memberRepository.save(new Member(null, email, encoded));
    }


    public String login(String email, String password) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(MemberService::invalidCredentials);
        validatePassword(password, member);
        return tokenProvider.create(member.getId());
    }


    private void validateNotDuplicated(String email) {
        if (memberRepository.existsByEmail(email)) {
            throw new DuplicateEmailException("이미 가입된 이메일입니다.");
        }
    }

    private void validatePassword(String rawPassword, Member member) {
        if (!passwordEncoder.matches(rawPassword, member.getPassword())) {
            throw invalidCredentials();
        }
    }

    private static InvalidCredentialsException invalidCredentials() {
        return new InvalidCredentialsException("이메일 또는 비밀번호가 올바르지 않습니다.");
    }
}
