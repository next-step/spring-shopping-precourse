package shopping.domains.user.out.jpa.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import shopping.domains.user.core.domain.entity.EncryptedEmail;
import shopping.domains.user.core.domain.entity.EncryptedPassword;
import shopping.domains.user.core.domain.entity.User;
import shopping.domains.user.out.jpa.entity.JpaUser;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static shopping.test.util.JpaTestUtils.persistAndFlush;

@DataJpaTest
class UserRepositoryTest {
    @Autowired
    private UserRepository repository;

    @Autowired
    private TestEntityManager testEntityManager;

    @Test
    @DisplayName("findByUser_Email test")
    void findByUser_EmailTest() {
        // given
        final String encryptedEmail = "encryptedEmail";
        final User user = User.builder()
                .email(new EncryptedEmail(encryptedEmail))
                .password(new EncryptedPassword("encryptedPassword"))
                .build();
        final JpaUser jpaUser = JpaUser.builder()
                .id(UUID.randomUUID())
                .user(user)
                .createdAt(LocalDateTime.now())
                .version(0L)
                .build();
        final JpaUser expected = persistAndFlush(jpaUser, testEntityManager);

        // when
        final Optional<JpaUser> actual = repository.findByUser_Email_Value(encryptedEmail);

        // then
        assertThat(actual.isPresent()).isTrue();
        assertThat(actual.get()).isEqualTo(expected);
    }
}