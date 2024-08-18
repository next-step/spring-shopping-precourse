package shopping.test.util;

import lombok.NonNull;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

public class JpaTestUtils {
    @NonNull
    public static <E> E persistAndFlush(
            @NonNull final E entity,
            @NonNull final TestEntityManager entityManager
    ) {
        final E merged = entityManager.merge(entity);
        return entityManager.persistAndFlush(merged);
    }
}
