package shopping;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import java.sql.SQLException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class MemberRegistrationRaceTest {

    @Mock private MemberRepository repository;

    @Mock private PasswordEncoder passwordEncoder;

    @ParameterizedTest
    @CsvSource({"23000, 1062", "23505, 0"})
    void translatesDatabaseDuplicateAfterPrecheck(String state, int code) {
        SQLException cause = new SQLException("duplicate email", state, code);
        given(repository.saveAndFlush(any(Member.class)))
                .willThrow(new DataIntegrityViolationException("duplicate", cause));
        MemberService service = new MemberService(repository, passwordEncoder);
        assertThatThrownBy(() -> service.register("member@example.com", "password123!"))
                .isInstanceOf(DuplicateEmailException.class);
    }

    @Test
    void doesNotMislabelOtherDatabaseErrorsAsDuplicateEmail() {
        SQLException cause = new SQLException("not null", "23502", 0);
        DataIntegrityViolationException failure =
                new DataIntegrityViolationException("not null", cause);
        given(repository.saveAndFlush(any(Member.class))).willThrow(failure);
        MemberService service = new MemberService(repository, passwordEncoder);
        assertThatThrownBy(() -> service.register("member@example.com", "password123!"))
                .isSameAs(failure);
    }
}
