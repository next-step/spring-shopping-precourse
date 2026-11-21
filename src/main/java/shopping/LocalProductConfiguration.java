package shopping;

import java.net.URI;
import java.net.http.HttpClient;
import java.time.Duration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration(proxyBeanMethods = false)
@Profile("local")
class LocalProductConfiguration {
    @Bean
    ProductRepository productRepository(ProductNameFilter filter) {
        return new FakeProductRepository(filter);
    }

    @Bean
    ProductNameFilter productNameFilter(HttpClient profanityHttpClient,
            @Value("${shopping.profanity.endpoint}") String endpoint) {
        return new PurgoMalumProductNameFilter(profanityHttpClient, URI.create(endpoint));
    }

    @Bean
    HttpClient profanityHttpClient() {
        return HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(2)).build();
    }
}
