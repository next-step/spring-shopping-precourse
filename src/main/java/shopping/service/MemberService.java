package shopping.service;

import shopping.domain.Email;
import shopping.domain.Member;
import shopping.domain.MemberId;
import shopping.domain.Members;
import shopping.domain.Password;

public class MemberService {
    private final Members members;
    private final TokenStore tokenStore;
    private long lastId = 0;

    public MemberService(Members members, TokenStore tokenStore) {
        validateNotNull(members, "회원 목록이 설정되지 않았습니다.");
        validateNotNull(tokenStore, "로그인 정보 저장소가 설정되지 않았습니다.");
        this.members = members;
        this.tokenStore = tokenStore;
    }

    // ID 부여와 추가를 한 덩어리로 실행하고, 추가에 성공했을 때만 ID를 소비한다.
    public synchronized String register(String email, String password) {
        Email memberEmail = new Email(email);
        Password memberPassword = new Password(password);
        Member member = new Member(new MemberId(lastId + 1), memberEmail, memberPassword);
        members.add(member);
        lastId++;
        return tokenStore.issue(member.id().orElseThrow());
    }

    public String login(String email, String password) {
        Member member = findMember(email, password);
        return tokenStore.issue(member.id().orElseThrow());
    }

    // 형식이 틀린 입력, 없는 이메일, 틀린 비밀번호를 모두 같은 실패로 처리한다.
    private Member findMember(String email, String password) {
        try {
            Member member = members.findByEmail(new Email(email))
                    .orElseThrow(LoginFailedException::new);
            return verify(member, new Password(password));
        } catch (IllegalArgumentException e) {
            throw new LoginFailedException();
        }
    }

    private Member verify(Member member, Password password) {
        if (!member.matchesPassword(password)) {
            throw new LoginFailedException();
        }
        return member;
    }

    private static void validateNotNull(Object value, String message) {
        if (value == null) {
            throw new NullPointerException(message);
        }
    }
}
