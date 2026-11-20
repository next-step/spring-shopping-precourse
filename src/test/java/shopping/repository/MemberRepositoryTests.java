package shopping.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import shopping.entity.Member;
import shopping.entity.PasswordEncoder;
import shopping.security.BCryptPasswordEncoderAdapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@Import(BCryptPasswordEncoderAdapter.class)
class MemberRepositoryTests {

    private static final String EMAIL = "user@example.com";
    private static final String PASSWORD = "password123";

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @DisplayName("회원을 저장하면 id가 생성된다")
    @Test
    void save() {
        Member saved = memberRepository.save(Member.of(EMAIL, PASSWORD, passwordEncoder));

        assertThat(saved.getId()).isNotNull();
    }

    @DisplayName("이메일로 회원 존재 여부를 확인한다")
    @Test
    void existsByEmail() {
        memberRepository.save(Member.of(EMAIL, PASSWORD, passwordEncoder));

        assertThat(memberRepository.existsByEmail(EMAIL)).isTrue();
        assertThat(memberRepository.existsByEmail("other@example.com")).isFalse();
    }

    @DisplayName("같은 이메일로 저장하면 유니크 제약 조건 위반 예외가 발생한다")
    @Test
    void duplicateEmailViolatesUniqueConstraint() {
        memberRepository.saveAndFlush(Member.of(EMAIL, PASSWORD, passwordEncoder));

        assertThatThrownBy(() -> memberRepository.saveAndFlush(Member.of(EMAIL, PASSWORD, passwordEncoder)))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @DisplayName("대소문자만 다른 이메일은 소문자로 정규화되어 유니크 제약 조건 위반 예외가 발생한다")
    @Test
    void emailsDifferingOnlyByCaseViolateUniqueConstraint() {
        memberRepository.saveAndFlush(Member.of("user@example.com", PASSWORD, passwordEncoder));

        assertThatThrownBy(() -> memberRepository.saveAndFlush(Member.of("USER@EXAMPLE.COM", PASSWORD, passwordEncoder)))
                .isInstanceOf(DataIntegrityViolationException.class);
    }
}
