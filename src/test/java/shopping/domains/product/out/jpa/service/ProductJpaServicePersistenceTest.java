package shopping.domains.product.out.jpa.service;

import lombok.NonNull;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import shopping.domains.product.core.domain.dto.ProductDto;
import shopping.domains.product.out.jpa.repository.ProductRepository;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(ProductJpaService.class)
class ProductJpaServicePersistenceTest {
    @Autowired
    private ProductJpaService productJpaService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private TestEntityManager testEntityManager;

    @ParameterizedTest
    @DisplayName("saveTest")
    @MethodSource("productDtoArguments")
    void saveTest(@NonNull final ProductDto dto) {
        // when
        final ProductDto actual = productJpaService.save(dto);
        final ProductDto expected = productRepository.findById(actual.getId())
                .get()
                .toDto();

        // then
        assertThat(actual).isNotNull();
        assertThat(actual).isEqualTo(expected);
    }

    private static Stream<Arguments> productDtoArguments() {
        return Stream.of(
                Arguments.of(ProductDto.builder()
                        .name("name")
                        .price(1000L)
                        .imageUrl("https://www.example.com/image.png")
                        .build()),
                Arguments.of(ProductDto.builder()
                        .id(UUID.randomUUID())
                        .name("name")
                        .price(1000L)
                        .imageUrl("https://www.example.com/image.png")
                        .createdAt(LocalDateTime.now())
                        .build()),
                Arguments.of(ProductDto.builder()
                        .id(UUID.randomUUID())
                        .name("name")
                        .price(1000L)
                        .imageUrl("https://www.example.com/image.png")
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .build()),
                Arguments.of(ProductDto.builder()
                        .id(UUID.randomUUID())
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .deletedAt(LocalDateTime.now())
                        .build())
        );
    }
}