package shopping.service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import shopping.domain.MemberId;
import shopping.domain.Member;
import shopping.domain.Members;

public class AuthService {
    private static final Pattern BEARER = Pattern.compile("^Bearer (\\S+)$",
            Pattern.CASE_INSENSITIVE);

    private final Members members;
    private final TokenStore tokenStore;

    public AuthService(Members members, TokenStore tokenStore) {
        validateNotNull(members, "회원 목록이 설정되지 않았습니다.");
        validateNotNull(tokenStore, "로그인 정보 저장소가 설정되지 않았습니다.");
        this.members = members;
        this.tokenStore = tokenStore;
    }

    // 헤더 없음, 형식 오류, 모르는 토큰을 모두 같은 실패로 처리한다.
    public Member authenticate(String authorizationHeader) {
        String token = extractToken(authorizationHeader);
        MemberId id = tokenStore.find(token).orElseThrow(UnauthorizedException::new);
        return members.findById(id).orElseThrow(UnauthorizedException::new);
    }

    private String extractToken(String header) {
        if (header == null) {
            throw new UnauthorizedException();
        }
        Matcher matcher = BEARER.matcher(header);
        if (!matcher.matches()) {
            throw new UnauthorizedException();
        }
        return matcher.group(1);
    }

    private static void validateNotNull(Object value, String message) {
        if (value == null) {
            throw new NullPointerException(message);
        }
    }
}
