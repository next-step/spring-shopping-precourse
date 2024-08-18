package shopping.member;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import shopping.model.Member;

public class MemberTest {
    @Test
    void 회원은_이메일을_가지고있다() {
        //given
        String email = "asd@asd.com";
        //when
        Member member = new Member(email);
        //then
        Assertions.assertThat(member.getEmail()).isEqualTo(email);
    }

}
