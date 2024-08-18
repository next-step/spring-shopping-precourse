package shopping.domains.user.out.jpa.service;

import lombok.NonNull;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import shopping.domains.user.core.domain.dto.TokenDto;
import shopping.domains.user.out.jpa.repository.TokenRepository;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(TokenJpaService.class)
class TokenJpaServicePersistenceTest {
    @Autowired
    private TokenJpaService tokenJpaService;

    @Autowired
    private TokenRepository tokenRepository;

    @Autowired
    private TestEntityManager testEntityManager;

    @ParameterizedTest
    @DisplayName("saveTest")
    @MethodSource("userDtoArguments")
    void saveTest(@NonNull final TokenDto dto) {
        // when
        final TokenDto actual = tokenJpaService.save(dto);
        final TokenDto expected = tokenRepository.findById(actual.getUserId())
                .get()
                .toDto();

        // then
        assertThat(actual).isNotNull();
        assertThat(actual).isEqualTo(expected);
    }

    private static Stream<Arguments> userDtoArguments() {
        return Stream.of(
                Arguments.of(TokenDto.builder()
                        .accessToken("accessToken")
                        .refreshToken("refreshToken")
                        .build()),
                Arguments.of(TokenDto.builder()
                        .userId(UUID.randomUUID())
                        .accessToken("accessToken")
                        .refreshToken("refreshToken")
                        .createdAt(LocalDateTime.now())
                        .build()),
                Arguments.of(TokenDto.builder()
                        .userId(UUID.randomUUID())
                        .accessToken("accessToken")
                        .refreshToken("refreshToken")
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .build()),
                Arguments.of(TokenDto.builder()
                        .userId(UUID.randomUUID())
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .deletedAt(LocalDateTime.now())
                        .build())
        );
    }
}