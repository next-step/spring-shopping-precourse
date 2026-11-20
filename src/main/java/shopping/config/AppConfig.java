package shopping.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import shopping.domain.Members;
import shopping.domain.Products;
import shopping.domain.ProfanityChecker;
import shopping.infrastructure.PurgoMalumProfanityChecker;
import shopping.infrastructure.PurgoMalumRestClientFactory;
import shopping.service.AuthService;
import shopping.service.MemberService;
import shopping.service.ProductService;
import shopping.service.TokenStore;
import shopping.service.WishService;

@Configuration
public class AppConfig {
    @Bean
    RestClient purgoMalumRestClient() {
        return PurgoMalumRestClientFactory.create(
                PurgoMalumRestClientFactory.BASE_URL,
                PurgoMalumRestClientFactory.CONNECT_TIMEOUT,
                PurgoMalumRestClientFactory.READ_TIMEOUT);
    }

    @Bean
    ProfanityChecker profanityChecker(RestClient purgoMalumRestClient) {
        return new PurgoMalumProfanityChecker(purgoMalumRestClient);
    }

    @Bean
    Products products() {
        return new Products();
    }

    @Bean
    ProductService productService(Products products, ProfanityChecker profanityChecker) {
        return new ProductService(products, profanityChecker);
    }

    @Bean
    Members members() {
        return new Members();
    }

    @Bean
    TokenStore tokenStore() {
        return new TokenStore();
    }

    @Bean
    MemberService memberService(Members members, TokenStore tokenStore) {
        return new MemberService(members, tokenStore);
    }

    @Bean
    AuthService authService(Members members, TokenStore tokenStore) {
        return new AuthService(members, tokenStore);
    }

    @Bean
    WishService wishService(ProductService productService) {
        return new WishService(productService);
    }
}
