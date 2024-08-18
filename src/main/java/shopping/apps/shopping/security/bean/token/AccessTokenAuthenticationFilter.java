package shopping.apps.shopping.security.bean.token;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;
import shopping.apps.shopping.api.common.ApiUrls;
import shopping.domains.common.core.domain.entity.UnauthorizedException;
import shopping.domains.common.core.domain.enums.CommonErrorCode;
import shopping.domains.user.core.domain.entity.TokenGenerator;

import java.io.IOException;
import java.util.UUID;


@Slf4j
public class AccessTokenAuthenticationFilter extends OncePerRequestFilter {
    private static final AntPathMatcher pathMatcher = new AntPathMatcher();
    private static final String AUTHORIZATION_HEADER_KEY = "Authorization";

    private static final String TOKEN_PREFIX = "Bearer ";

    private final TokenGenerator tokenGenerator;

    /**
     * AccessTokenFilter를 적용하지 않는 URI 목록입니다.
     */
    private final AccessTokenWhitelist accessTokenWhitelist;

    public AccessTokenAuthenticationFilter(
            @NonNull final TokenGenerator tokenGenerator,
            @NonNull final AccessTokenWhitelist accessTokenWhitelist
    ) {
        this.tokenGenerator = tokenGenerator;
        this.accessTokenWhitelist = accessTokenWhitelist;
    }

    @Override
    protected void doFilterInternal(
            @NonNull final HttpServletRequest request,
            @NonNull final HttpServletResponse response,
            @NonNull final FilterChain filterChain
    )
            throws ServletException, IOException {
        final String requestURI = request.getRequestURI();

        if (!requestURI.startsWith(ApiUrls.API_PREFIX)) {
            throw new IllegalArgumentException("Invalid API prefix in request URI: " + requestURI);
        }

        final String trimmedRequestURI = requestURI.substring(ApiUrls.API_PREFIX.length());

        if (accessTokenWhitelist.noneMatch(trimmedRequestURI, pathMatcher::match)) {
            setDetails(request, getTokenFromRequest(request));
        }

        filterChain.doFilter(request, response);
    }

    /**
     * 인증 정보에 id 값을 저장합니다.
     */
    private void setDetails(
            @NonNull final HttpServletRequest request,
            @NonNull final String token
    ) {
        final UUID userId = tokenGenerator.getUuidFromToken(token);
        final CustomUserDetails userDetails = new CustomUserDetails(userId);
        final UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities()
        );

        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    /**
     * Authorization Header에서 Jwt access token 값을 가져옵니다.
     */
    private String getTokenFromRequest(@NonNull final HttpServletRequest request) {
        final String bearerToken = request.getHeader(AUTHORIZATION_HEADER_KEY);

        if (bearerToken != null && bearerToken.startsWith(TOKEN_PREFIX)) {
            return bearerToken.substring(TOKEN_PREFIX.length());
        }

        throw new UnauthorizedException(CommonErrorCode.UNAUTHORIZED);
    }
}
