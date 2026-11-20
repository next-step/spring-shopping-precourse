package shopping;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

@DataJpaTest(
        properties = {
            "spring.flyway.enabled=false",
            "spring.datasource.password=",
            "spring.jpa.hibernate.ddl-auto=create-drop"
        })
class MemberRepositoryTest {

    @Autowired private MemberRepository memberRepository;

    @Autowired private EntityManager entityManager;

    @Test
    void savesAndFindsMemberByEmail() {
        Member saved =
                memberRepository.save(new Member("member@example.com", "test-password-hash"));
        entityManager.flush();
        entityManager.clear();

        Member found = memberRepository.findByEmail("member@example.com").orElseThrow();

        assertThat(found.getId()).isNotNull();
        assertThat(found.getId()).isEqualTo(saved.getId());
        assertThat(found.getEmail()).isEqualTo("member@example.com");
        assertThat(found.getPasswordHash()).isEqualTo("test-password-hash");
    }

    @Test
    void returnsEmptyWhenEmailDoesNotExist() {
        assertThat(memberRepository.findByEmail("missing@example.com")).isEmpty();
    }

    @Test
    void existsByEmailReturnsTrueForRegisteredEmail() {
        memberRepository.saveAndFlush(new Member("member@example.com", "test-password-hash"));

        assertThat(memberRepository.existsByEmail("member@example.com")).isTrue();
    }

    @Test
    void existsByEmailReturnsFalseForUnknownEmail() {
        assertThat(memberRepository.existsByEmail("missing@example.com")).isFalse();
    }

    @Test
    void rejectsDuplicateEmail() {
        memberRepository.saveAndFlush(new Member("member@example.com", "first-hash"));

        assertThatThrownBy(
                        () ->
                                memberRepository.saveAndFlush(
                                        new Member("member@example.com", "second-hash")))
                .isInstanceOf(DataIntegrityViolationException.class);
    }
}
