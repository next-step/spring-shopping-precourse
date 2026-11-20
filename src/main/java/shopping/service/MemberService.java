package shopping.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import shopping.domain.Member;
import shopping.repository.MemberRepository;
import shopping.repository.TokenRepository;

@Service
public class MemberService {

    private final MemberRepository memberRepository;
    private final TokenRepository tokenRepository;

    public MemberService(MemberRepository memberRepository, TokenRepository tokenRepository) {
        this.memberRepository = memberRepository;
        this.tokenRepository = tokenRepository;
    }

    public String register(String email, String password) {
        Member member = memberRepository.findByEmail(email);

        if (member != null) {
            throw new IllegalArgumentException("이미 등록된 이메일입니다.");
        }

        Member newMember = memberRepository.save(new Member(null, email, password));
        String token = UUID.randomUUID().toString();
        tokenRepository.save(token, newMember.id());

        return token;
    }

    public String login(String email, String password) {
        Member member = memberRepository.findByEmail(email);

        if (member == null || !member.hasPassword(password)) {
            throw new IllegalArgumentException("이메일 또는 비밀번호가 일치하지 않습니다.");
        }

        String token = UUID.randomUUID().toString();
        tokenRepository.save(token, member.id());

        return token;
    }
}
