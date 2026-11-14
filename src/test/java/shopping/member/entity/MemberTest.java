package shopping.member.entity;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

class MemberTest {
    @Test
    void 회원을_생성하면_값이_저장된다(){
        // given & when
        Member member = new Member("홍길동", "gildong@joson.kr", "fathermyfather12");

        //then
        assertThat(member.getName()).isEqualTo("홍길동");
        assertThat(member.getEmail()).isEqualTo("gildong@joson.kr");
        assertThat(member.getPassword()).isEqualTo("fathermyfather12");
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" "})
    void 이메일이_비어있으면_예외가_발생한다(String email){
        assertThatThrownBy(() -> new Member("홍길동", email, "fathermyfather12"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" "})
    void 비밀번호가_비어있으면_예외가_발생한다(String password){
        assertThatThrownBy(() -> new Member("홍길동", "gildong@joson.kr", password))
                .isInstanceOf(IllegalArgumentException.class);
    }


}