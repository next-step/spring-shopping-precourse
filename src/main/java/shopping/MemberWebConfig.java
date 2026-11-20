package shopping;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MemberWebConfig implements WebMvcConfigurer {

    private final MemberAuthenticator authenticator;

    public MemberWebConfig(MemberAuthenticator authenticator) {
        this.authenticator = authenticator;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new MemberAuthenticationInterceptor(authenticator))
                .addPathPatterns("/api/wishes", "/api/wishes/**");
    }
}
