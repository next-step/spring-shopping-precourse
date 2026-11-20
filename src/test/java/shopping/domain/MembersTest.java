package shopping.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class MembersTest {
    private Members members;

    private static Member member(long id, String email) {
        return new Member(new MemberId(id), new Email(email), new Password("abcd123!"));
    }

    @BeforeEach
    void setUp() {
        members = new Members();
    }

    @Test
    @DisplayName("회원을 추가하면 ID와 이메일로 찾을 수 있다")
    void addAndFind() {
        Member member = member(1, "user@gmail.com");

        members.add(member);

        assertThat(members.findById(new MemberId(1))).contains(member);
        assertThat(members.findByEmail(new Email("user@gmail.com"))).contains(member);
    }

    @Test
    @DisplayName("없는 ID나 이메일은 비어 있는 결과를 돌려준다")
    void findNotFound() {
        members.add(member(1, "user@gmail.com"));

        assertThat(members.findById(new MemberId(9))).isEmpty();
        assertThat(members.findByEmail(new Email("none@gmail.com"))).isEmpty();
    }

    @Test
    @DisplayName("같은 ID의 회원은 추가할 수 없고 기존 회원은 유지된다")
    void addDuplicateId() {
        Member first = member(1, "first@gmail.com");
        members.add(first);

        assertThatThrownBy(() -> members.add(member(1, "second@gmail.com")))
                .isInstanceOf(IllegalArgumentException.class);
        assertThat(members.findById(new MemberId(1))).contains(first);
        assertThat(members.findByEmail(new Email("second@gmail.com"))).isEmpty();
    }

    @Test
    @DisplayName("같은 이메일의 회원은 ID가 달라도 추가할 수 없고 기존 회원은 유지된다")
    void addDuplicateEmail() {
        Member first = member(1, "user@gmail.com");
        members.add(first);

        assertThatThrownBy(() -> members.add(member(2, "user@gmail.com")))
                .isInstanceOf(IllegalArgumentException.class);
        assertThat(members.findByEmail(new Email("user@gmail.com"))).contains(first);
        assertThat(members.findById(new MemberId(2))).isEmpty();
    }

    @Test
    @DisplayName("ID와 이메일이 모두 다르면 추가할 수 있다")
    void addDifferentMembers() {
        members.add(member(1, "first@gmail.com"));

        members.add(member(2, "second@gmail.com"));

        assertThat(members.findById(new MemberId(2))).isPresent();
    }

    @Test
    @DisplayName("ID가 없는 회원은 추가할 수 없다")
    void addWithoutId() {
        Member unsaved = new Member(new Email("user@gmail.com"), new Password("abcd123!"));

        assertThatThrownBy(() -> members.add(unsaved))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("null은 추가할 수 없다")
    void addNull() {
        assertThatThrownBy(() -> members.add(null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("null ID나 null 이메일로 찾으면 예외가 발생한다")
    void findNull() {
        assertThatThrownBy(() -> members.findById(null))
                .isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> members.findByEmail(null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("같은 이메일로 동시에 가입해도 한 명만 추가된다")
    void addSameEmailConcurrently() throws Exception {
        for (int round = 0; round < 200; round++) {
            Members concurrent = new Members();
            List<Member> candidates = new ArrayList<>();
            for (int i = 1; i <= 8; i++) {
                candidates.add(member(i, "same@gmail.com"));
            }

            assertThat(addTogether(concurrent, candidates)).isEqualTo(1);
        }
    }

    @Test
    @DisplayName("같은 ID로 동시에 가입해도 한 명만 추가된다")
    void addSameIdConcurrently() throws Exception {
        for (int round = 0; round < 200; round++) {
            Members concurrent = new Members();
            List<Member> candidates = new ArrayList<>();
            for (int i = 1; i <= 8; i++) {
                candidates.add(member(1, "user" + i + "@gmail.com"));
            }

            assertThat(addTogether(concurrent, candidates)).isEqualTo(1);
        }
    }

    private static long addTogether(Members target, List<Member> candidates) throws Exception {
        ExecutorService executor = Executors.newFixedThreadPool(candidates.size());
        CountDownLatch start = new CountDownLatch(1);
        List<Future<Boolean>> results = new ArrayList<>();
        for (Member candidate : candidates) {
            results.add(executor.submit(() -> addWhenStarted(target, start, candidate)));
        }
        start.countDown();

        long successes = 0;
        for (Future<Boolean> result : results) {
            if (result.get(10, TimeUnit.SECONDS)) {
                successes++;
            }
        }
        executor.shutdownNow();
        return successes;
    }

    private static boolean addWhenStarted(Members target, CountDownLatch start, Member member)
            throws InterruptedException {
        start.await();
        try {
            target.add(member);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
