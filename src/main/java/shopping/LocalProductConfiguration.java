package shopping;

import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration(proxyBeanMethods = false)
@Profile("local")
class LocalProductConfiguration {
    @Bean
    ProductRepository productRepository() {
        return new FakeProductRepository(new DefaultProductNameFilter(List.of()));
    }
}
