package shopping;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration(proxyBeanMethods = false)
@Profile("local")
class LocalWishConfiguration {
    @Bean
    WishRepository wishRepository() {
        return new FakeWishRepository();
    }

    @Bean
    WishList wishList(TokenRepository tokens, ProductRepository products, WishRepository wishes) {
        return new WishList(tokens, products, wishes);
    }
}
