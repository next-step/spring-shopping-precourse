package shopping.auth;

import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import shopping.common.exception.UnauthorizedException;

@Component
public class LoginMemberArgumentResolver implements HandlerMethodArgumentResolver {

    private static final String BEARER_PREFIX = "Bearer ";

    private final TokenProvider tokenProvider;

    public LoginMemberArgumentResolver(TokenProvider tokenProvider) {
        this.tokenProvider = tokenProvider;
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(LoginMember.class)
                && Long.class.equals(parameter.getParameterType());
    }

    @Override
    public Object resolveArgument(
            MethodParameter parameter,
            ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest,
            WebDataBinderFactory binderFactory) {
        String header = webRequest.getHeader(HttpHeaders.AUTHORIZATION);
        return tokenProvider.extractMemberId(extractToken(header));
    }

    private String extractToken(String header) {
        if (header == null || !header.startsWith(BEARER_PREFIX)) {
            throw new UnauthorizedException("인증 토큰이 필요합니다.");
        }
        return header.substring(BEARER_PREFIX.length());
    }
}
