package shopping;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.web.servlet.HandlerInterceptor;

public class MemberAuthenticationInterceptor implements HandlerInterceptor {

    public static final String MEMBER_ID = "authenticatedMemberId";
    private final MemberAuthenticator authenticator;

    public MemberAuthenticationInterceptor(MemberAuthenticator authenticator) {
        this.authenticator = authenticator;
    }

    @Override
    public boolean preHandle(
            HttpServletRequest request, HttpServletResponse response, Object handler) {
        Long memberId = authenticator.authenticate(request.getHeader(HttpHeaders.AUTHORIZATION));
        request.setAttribute(MEMBER_ID, memberId);
        return true;
    }
}
