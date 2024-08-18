package shopping.apps.shopping.security.config;

import lombok.NonNull;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.encrypt.AesBytesEncryptor;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import shopping.apps.shopping.security.bean.token.AccessTokenAuthenticationFilter;
import shopping.apps.shopping.security.bean.token.JwtTokenGenerator;
import shopping.apps.shopping.security.bean.token.AccessTokenWhitelist;
import shopping.domains.user.core.domain.entity.TokenGenerator;

@Configuration
public class SecurityConfig {
    @Value("${security.encrypt.aes.secret}")
    private String aesSecret;

    @Value("${security.encrypt.aes.salt}")
    private String aesSalt;

    @Bean
    public AesBytesEncryptor aesBytesEncryptor() {
        return new AesBytesEncryptor(aesSecret, aesSalt);
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public JwtTokenGenerator accessTokenGenerator(
            @Value("${security.encrypt.token.access.secret}") final String secret,
            @Value("${security.encrypt.token.access.expire}") final String expire
    ) {
        return new JwtTokenGenerator(secret, Long.parseLong(expire));
    }

    @Bean
    public JwtTokenGenerator refreshTokenGenerator(
            @Value("${security.encrypt.token.refresh.secret}") final String secret,
            @Value("${security.encrypt.token.refresh.expire}") final String expire
    ) {
        return new JwtTokenGenerator(secret, Long.parseLong(expire));
    }

    @Bean
    public SecurityFilterChain filterChain(
            @NonNull final HttpSecurity httpSecurity,
            @Qualifier("accessTokenGenerator") @NonNull final TokenGenerator tokenGenerator
    ) throws Exception {
        final AccessTokenWhitelist accessTokenWhitelist = AccessTokenWhitelist.getInstance();

        return httpSecurity
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(accessTokenWhitelist.toArray()).permitAll()
                        .anyRequest().authenticated()
                )
                .csrf(AbstractHttpConfigurer::disable)
                .cors(AbstractHttpConfigurer::disable)
                .addFilterBefore(new AccessTokenAuthenticationFilter(tokenGenerator, accessTokenWhitelist), UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}
