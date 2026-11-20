package shopping.auth;

import org.springframework.stereotype.Component;
import shopping.member.Member;
import shopping.member.MemberRepository;

@Component
public class Authenticator {
    private static final String PREFIX = "Bearer ";

    private final JwtTokenProvider tokenProvider;
    private final MemberRepository memberRepository;

    public Authenticator(JwtTokenProvider tokenProvider, MemberRepository memberRepository) {
        this.tokenProvider = tokenProvider;
        this.memberRepository = memberRepository;
    }

    public Member authenticate(String authorization) {
        String email = tokenProvider.extractEmail(extractToken(authorization));
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new InvalidTokenException("유효하지 않은 토큰입니다."));
    }

    private String extractToken(String authorization) {
        if (authorization == null || !authorization.startsWith(PREFIX)) {
            throw new InvalidTokenException("Authorization 헤더가 필요합니다.");
        }
        return authorization.substring(PREFIX.length());
    }
}