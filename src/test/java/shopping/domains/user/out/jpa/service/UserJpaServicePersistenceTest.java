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
import shopping.domains.user.core.domain.dto.UserDto;
import shopping.domains.user.out.jpa.entity.JpaUser;
import shopping.domains.user.out.jpa.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static shopping.test.util.JpaTestUtils.persistAndFlush;

@DataJpaTest
@Import(UserJpaService.class)
class UserJpaServicePersistenceTest {
    @Autowired
    private UserJpaService userJpaService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestEntityManager testEntityManager;

    @ParameterizedTest
    @DisplayName("saveTest")
    @MethodSource("userDtoArguments")
    void saveTest(@NonNull final UserDto dto) {
        // when
        final UserDto actual = userJpaService.save(dto);
        final UserDto expected = userRepository.findById(actual.getId())
                .get()
                .toDto();

        // then
        assertThat(actual).isNotNull();
        assertThat(actual).isEqualTo(expected);
    }

    @ParameterizedTest
    @DisplayName("findUserByEmailTest")
    @MethodSource("userDtoArguments")
    void findUserByEmailTest(@NonNull final UserDto dto) {
        // given
        final JpaUser savedEntity = persistAndFlush(new JpaUser(dto), testEntityManager);
        final UserDto expected = savedEntity.toDto();

        // when
        final Optional<UserDto> actual = userJpaService.findUser(expected.getEncryptedEmail());

        // then
        assertThat(actual.isPresent()).isTrue();
        assertThat(actual.get()).isEqualTo(expected);
    }

    private static Stream<Arguments> userDtoArguments() {
        return Stream.of(
                Arguments.of(UserDto.builder()
                        .encryptedEmail("encryptedEmail")
                        .encryptedPassword("encryptedPassword")
                        .build()),
                Arguments.of(UserDto.builder()
                        .id(UUID.randomUUID())
                        .encryptedEmail("encryptedEmail")
                        .encryptedPassword("encryptedPassword")
                        .createdAt(LocalDateTime.now())
                        .build()),
                Arguments.of(UserDto.builder()
                        .id(UUID.randomUUID())
                        .encryptedEmail("encryptedEmail")
                        .encryptedPassword("encryptedPassword")
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .build()),
                Arguments.of(UserDto.builder()
                        .id(UUID.randomUUID())
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .deletedAt(LocalDateTime.now())
                        .build())
        );
    }
}