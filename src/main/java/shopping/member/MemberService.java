package shopping.member;

import org.springframework.stereotype.Service;
import shopping.auth.JwtTokenProvider;

@Service
public class MemberService {
    private final MemberRepository repository;
    private final JwtTokenProvider tokenProvider;

    public MemberService(MemberRepository repository, JwtTokenProvider tokenProvider) {
        this.repository = repository;
        this.tokenProvider = tokenProvider;
    }

    public String register(String email, String password) {
        Member member = new Member(null, email, password);
        validateNotDuplicated(member.getEmail());
        Member saved = repository.save(member);
        return tokenProvider.createToken(saved.getEmail());
    }

    public String login(String email, String password) {
        Member member = repository.findByEmail(email).orElseThrow(LoginFailedException::new);
        if (!member.matchesPassword(password)) {
            throw new LoginFailedException();
        }
        return tokenProvider.createToken(member.getEmail());
    }

    private void validateNotDuplicated(String email) {
        if (repository.findByEmail(email).isPresent()) {
            throw new DuplicateEmailException();
        }
    }
}