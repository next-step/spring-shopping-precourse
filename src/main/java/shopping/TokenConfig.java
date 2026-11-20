package shopping;

import java.time.Clock;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TokenConfig {

    @Bean
    public Clock tokenClock() {
        return Clock.systemUTC();
    }
}
