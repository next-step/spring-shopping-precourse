package shopping.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import shopping.domain.Members;
import shopping.domain.Products;
import shopping.domain.ProfanityChecker;
import shopping.infrastructure.PurgoMalumProfanityChecker;
import shopping.service.MemberService;
import shopping.service.ProductService;
import shopping.service.TokenStore;

@SpringBootTest
@Import(BeanWiringTest.FakeProfanityConfig.class)
class BeanWiringTest {
    @TestConfiguration
    static class FakeProfanityConfig {
        @Bean
        @Primary
        ProfanityChecker fakeProfanityChecker() {
            return text -> text.contains("shit");
        }
    }

    @Autowired
    private ApplicationContext context;

    @Autowired
    private ProductService productService;

    @Autowired
    private MemberService memberService;

    @Autowired
    private TokenStore tokenStore;

    @Test
    @DisplayName("실제 비속어 검사기는 PurgoMalum 구현체로 등록된다")
    void realCheckerIsPurgoMalum() {
        assertThat(context.getBean("profanityChecker"))
                .isInstanceOf(PurgoMalumProfanityChecker.class);
    }

    @Test
    @DisplayName("Products는 하나만 만들어져 공유된다")
    void productsIsSingleton() {
        assertThat(context.getBeansOfType(Products.class)).hasSize(1);
    }

    @Test
    @DisplayName("상품 서비스는 비속어 검사기 빈을 사용해 이름을 검사한다")
    void serviceUsesProfanityChecker() {
        assertThatThrownBy(() -> productService.create("shit", 1000, "https://a.com/a.png"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("상품 서비스가 만든 상품은 같은 서비스에서 조회된다")
    void serviceStoresProducts() {
        productService.create("WiringItem", 1000, "https://a.com/a.png");

        assertThat(productService.list()).anyMatch(p -> p.name().value().equals("WiringItem"));
    }

    @Test
    @DisplayName("Members와 TokenStore도 하나만 만들어져 공유된다")
    void membersAndTokenStoreAreSingletons() {
        assertThat(context.getBeansOfType(Members.class)).hasSize(1);
        assertThat(context.getBeansOfType(TokenStore.class)).hasSize(1);
    }

    @Test
    @DisplayName("회원 서비스가 발급한 토큰은 공유된 토큰 저장소에서 확인된다")
    void memberServiceUsesSharedTokenStore() {
        String token = memberService.register("wiring@gmail.com", "abcd123!");

        assertThat(tokenStore.find(token)).isPresent();
    }
}
