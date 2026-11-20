package shopping;

import java.util.Map;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration(proxyBeanMethods = false)
@Profile("local")
class LocalAuthenticationConfiguration {
    @Bean
    MemberRepository memberRepository() {
        return new FakeMemberRepository(Map.of(), 1L);
    }

    @Bean
    TokenRepository tokenRepository() {
        return new InMemoryTokenRepository();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new PlainPasswordEncoder();
    }

    @Bean
    Authentication authentication(MemberRepository members, PasswordEncoder encoder, TokenRepository tokens) {
        return new Authentication(new Registration(members, encoder), new Login(members, encoder), tokens);
    }
}
