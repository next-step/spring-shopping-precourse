package shopping.domain;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public class Members {
    // 회원 ID를 키로 쓰므로 같은 ID의 회원은 하나만 담긴다. 이메일 중복은 따로 검사한다.
    private final Map<MemberId, Member> values = new LinkedHashMap<>();

    // ID·이메일 중복 검사와 추가가 한 덩어리로 실행되어야 동시 가입에서도 중복을 막을 수 있다.
    public synchronized void add(Member member) {
        validateNotNull(member, "회원 정보가 없습니다.");
        MemberId id = idOf(member);
        validateIdNotDuplicated(id);
        validateEmailNotDuplicated(member.email());
        values.put(id, member);
    }

    public synchronized Optional<Member> findById(MemberId id) {
        validateNotNull(id, "회원 번호가 없습니다.");
        return Optional.ofNullable(values.get(id));
    }

    public synchronized Optional<Member> findByEmail(Email email) {
        validateNotNull(email, "이메일 정보가 없습니다.");
        return values.values().stream()
                .filter(member -> member.hasEmail(email))
                .findFirst();
    }

    private MemberId idOf(Member member) {
        return member.id().orElseThrow(
                () -> new IllegalArgumentException("아직 가입되지 않은 회원은 추가할 수 없습니다."));
    }

    private void validateIdNotDuplicated(MemberId id) {
        if (values.containsKey(id)) {
            throw new IllegalArgumentException("이미 등록된 회원 번호입니다: " + id.value());
        }
    }

    private void validateEmailNotDuplicated(Email email) {
        if (findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("이미 가입된 이메일입니다.");
        }
    }

    private static void validateNotNull(Object value, String message) {
        if (value == null) {
            throw new NullPointerException(message);
        }
    }
}
