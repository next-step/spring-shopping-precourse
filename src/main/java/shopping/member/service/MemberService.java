package shopping.member.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shopping.auth.JwtProvider;
import shopping.common.exception.BusinessException;
import shopping.common.exception.ErrorCode;
import shopping.member.dto.LoginRequest;
import shopping.member.dto.LoginResponse;
import shopping.member.dto.SignupRequest;
import shopping.member.dto.SignupResponse;
import shopping.member.entity.Member;
import shopping.member.repository.MemberRepository;

import java.util.Locale;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    @Transactional
    public SignupResponse signup(SignupRequest request) {
        String email = normalizeEmail(request.email());

        if (memberRepository.existsByEmail(email)) {
            throw new BusinessException(ErrorCode.DUPLICATE_EMAIL);
        }

        String passwordHash = passwordEncoder.encode(request.password());
        Member member = new Member(email, passwordHash, request.name());
        memberRepository.save(member);

        return new SignupResponse(jwtProvider.createToken(member.getId()));
    }

    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {
        String email = normalizeEmail(request.email());

        Member member = memberRepository.findByEmail(email)
                .filter(found ->
                        passwordEncoder.matches(request.password(), found.getPasswordHash()))
                .orElseThrow(() -> new BusinessException(ErrorCode.FAIL_LOGIN));

        return new LoginResponse(jwtProvider.createToken(member.getId()));
    }

    private String normalizeEmail(String email) {
        return email.toLowerCase(Locale.ROOT);
    }
}
