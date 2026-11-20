package shopping.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import shopping.domain.MemberId;

class TokenStoreTest {
    private static final MemberId ID = new MemberId(1);

    private TokenStore store;

    @BeforeEach
    void setUp() {
        store = new TokenStore();
    }

    @Test
    @DisplayName("발급한 토큰은 UUID 형식이다")
    void issueUuid() {
        String token = store.issue(ID);

        assertThat(UUID.fromString(token)).hasToString(token);
    }

    @Test
    @DisplayName("발급한 토큰으로 회원 ID를 찾을 수 있다")
    void find() {
        String token = store.issue(ID);

        assertThat(store.find(token)).contains(ID);
    }

    @Test
    @DisplayName("같은 회원에게 여러 번 발급하면 매번 다른 토큰이고 모두 유효하다")
    void issueManyForSameEmail() {
        String first = store.issue(ID);
        String second = store.issue(ID);

        assertThat(first).isNotEqualTo(second);
        assertThat(store.find(first)).contains(ID);
        assertThat(store.find(second)).contains(ID);
    }

    @Test
    @DisplayName("모르는 토큰, null, 빈 값은 비어 있는 결과를 돌려준다")
    void findUnknown() {
        store.issue(ID);

        assertThat(store.find(UUID.randomUUID().toString())).isEmpty();
        assertThat(store.find("not-a-token")).isEmpty();
        assertThat(store.find("")).isEmpty();
        assertThat(store.find(null)).isEmpty();
    }

    @Test
    @DisplayName("null 회원 ID로는 발급할 수 없다")
    void issueNull() {
        assertThatThrownBy(() -> store.issue(null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("토큰은 서로 겹치지 않는다")
    void tokensAreUnique() {
        Set<String> tokens = new HashSet<>();
        for (int i = 0; i < 1000; i++) {
            tokens.add(store.issue(ID));
        }

        assertThat(tokens).hasSize(1000);
    }
}
