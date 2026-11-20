package shopping;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest(
        properties = {
            "spring.datasource.url=jdbc:h2:mem:member-auth-rollback;DB_CLOSE_DELAY=-1",
            "spring.datasource.driver-class-name=org.h2.Driver",
            "spring.datasource.username=sa",
            "spring.datasource.password=",
            "spring.jpa.hibernate.ddl-auto=create-drop",
            "spring.flyway.enabled=false"
        })
class MemberAuthRollbackTest {

    @Autowired private MemberAuthService authService;

    @Autowired private MemberRepository members;

    @MockitoBean private TokenService tokenService;

    @Test
    void failedTokenIssueRollsBackNewMember() {
        given(tokenService.issue(any(Member.class)))
                .willThrow(new IllegalStateException("token storage failed"));
        assertThatThrownBy(() -> authService.register("rollback@example.com", "password123!"))
                .isInstanceOf(IllegalStateException.class);
        assertThat(members.existsByEmail("rollback@example.com")).isFalse();
    }
}
