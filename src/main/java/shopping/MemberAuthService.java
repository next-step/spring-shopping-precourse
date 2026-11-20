package shopping;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MemberAuthService {

    private final MemberService members;
    private final TokenService tokens;

    public MemberAuthService(MemberService members, TokenService tokens) {
        this.members = members;
        this.tokens = tokens;
    }

    @Transactional
    public AuthTokenResponse register(String email, String password) {
        Member member = members.register(email, password);
        return tokens.issue(member);
    }

    @Transactional
    public AuthTokenResponse login(String email, String password) {
        Member member = members.login(email, password);
        return tokens.issue(member);
    }
}
