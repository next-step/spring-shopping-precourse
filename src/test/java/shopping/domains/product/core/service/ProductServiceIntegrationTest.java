package shopping.domains.product.core.service;

import com.github.tomakehurst.wiremock.client.WireMock;
import org.assertj.core.api.ThrowableAssert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.test.context.TestPropertySource;
import shopping.domains.common.core.domain.entity.ClientIllegalArgumentException;
import shopping.domains.product.core.domain.service.ProductService;
import shopping.domains.product.core.in.command.CreateProductCommand;
import shopping.domains.product.core.domain.dto.ProductDto;
import shopping.domains.product.out.jpa.entity.JpaProduct;
import shopping.domains.product.out.jpa.repository.ProductRepository;

import java.util.Optional;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatException;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@AutoConfigureWireMock(port = 0)
@TestPropertySource(properties = {
        "spring.cloud.openfeign.client.config.purgoMalumClient.url=http://localhost:${wiremock.server.port}"
})
class ProductServiceIntegrationTest {
    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;

    @BeforeEach
    void setup() {
        WireMock.stubFor(get(urlPathEqualTo("/containsprofanity"))
                .withQueryParam("text", equalTo("badword"))
                .willReturn(aResponse().withBody("true")));

        WireMock.stubFor(get(urlPathEqualTo("/containsprofanity"))
                .withQueryParam("text", equalTo("goodword"))
                .willReturn(aResponse().withBody("false")));
    }

    @Test
    @DisplayName("상품 생성 테스트")
    void createProductTest() {
        // given
        final CreateProductCommand command = CreateProductCommand.builder()
                .name("goodword")
                .imageUrl("https://www.example.com/image.png")
                .price(1000L)
                .build();

        // when
        final ProductDto actual = productService.createProduct(command);
        final Optional<JpaProduct> expected = productRepository.findById(actual.getId());

        // then
        assertThat(expected.isPresent()).isTrue();
        assertThat(actual).isEqualTo(expected.get().toDto());
    }

    @Test
    @DisplayName("상품 생성 예외 테스트")
    void createProductExceptionTest() {
        // given
        final CreateProductCommand command = CreateProductCommand.builder()
                .name("badword")
                .imageUrl("https://www.example.com/image.png")
                .price(1000L)
                .build();

        // when
        final ThrowableAssert.ThrowingCallable actual = () -> productService.createProduct(command);

        // then
        assertThatException().isThrownBy(actual).isInstanceOf(ClientIllegalArgumentException.class);
    }
}