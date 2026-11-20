package shopping.repository;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import shopping.model.Member;

class MemberRepositoryTest {
    private MemberRepository memberRepository;

    @BeforeEach
    void setUp() {
        memberRepository = new MemberRepository();
    }

    @Test
    @DisplayName("회원을 저장하면 ID가 생성된다")
    void assignIdWhenSavingMember() {
        Member member = memberRepository.save(new Member("user@example.com", "password"));

        assertThat(member.getId()).isNotNull();
    }

    @Test
    @DisplayName("이메일로 회원을 조회할 수 있다")
    void findMemberByEmail() {
        Member member = memberRepository.save(new Member("user@example.com", "password"));

        assertThat(memberRepository.findByEmail("user@example.com"))
                .contains(member);
    }
}
