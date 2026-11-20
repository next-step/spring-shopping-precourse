package shopping.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ProductsConcurrencyTest {
    private static final ProfanityChecker NO_PROFANITY = text -> false;
    private static final int THREADS = 8;
    private static final int ROUNDS = 300;

    private static Product product(long id, String name) {
        return new Product(new ProductId(id), new ProductName(name, NO_PROFANITY),
                new ProductPrice(1000), new ProductImage("https://example.com/a.png"));
    }

    private static boolean succeeds(Runnable action) {
        try {
            action.run();
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    private static long runTogether(List<Runnable> actions) throws Exception {
        ExecutorService executor = Executors.newFixedThreadPool(actions.size());
        CountDownLatch start = new CountDownLatch(1);
        List<Future<Boolean>> results = new ArrayList<>();
        for (Runnable action : actions) {
            results.add(executor.submit(gated(start, action)));
        }
        start.countDown();
        long successes = countSuccesses(results);
        executor.shutdownNow();
        return successes;
    }

    private static Callable<Boolean> gated(CountDownLatch start, Runnable action) {
        return () -> {
            start.await();
            return succeeds(action);
        };
    }

    private static long countSuccesses(List<Future<Boolean>> results) throws Exception {
        long successes = 0;
        for (Future<Boolean> result : results) {
            if (result.get(10, TimeUnit.SECONDS)) {
                successes++;
            }
        }
        return successes;
    }

    @Test
    @DisplayName("같은 이름의 상품을 동시에 추가해도 하나만 들어간다")
    void addSameNameConcurrently() throws Exception {
        for (int round = 0; round < ROUNDS; round++) {
            Products products = new Products();
            List<Runnable> actions = new ArrayList<>();
            for (int i = 1; i <= THREADS; i++) {
                Product product = product(i, "Same");
                actions.add(() -> products.add(product));
            }

            long successes = runTogether(actions);

            assertThat(successes).isEqualTo(1);
            assertThat(products.all()).hasSize(1);
        }
    }

    @Test
    @DisplayName("같은 ID의 상품을 동시에 추가해도 하나만 들어간다")
    void addSameIdConcurrently() throws Exception {
        for (int round = 0; round < ROUNDS; round++) {
            Products products = new Products();
            List<Runnable> actions = new ArrayList<>();
            for (int i = 1; i <= THREADS; i++) {
                Product product = product(1, "Name" + i);
                actions.add(() -> products.add(product));
            }

            long successes = runTogether(actions);

            assertThat(successes).isEqualTo(1);
            assertThat(products.all()).hasSize(1);
        }
    }

    @Test
    @DisplayName("서로 다른 상품은 동시에 추가해도 모두 들어간다")
    void addDifferentProductsConcurrently() throws Exception {
        int count = 400;
        Products products = new Products();
        List<Runnable> actions = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            Product product = product(i, "Name" + i);
            actions.add(() -> products.add(product));
        }

        long successes = runTogether(actions);

        assertThat(successes).isEqualTo(count);
        assertThat(products.all()).hasSize(count);
    }

    @Test
    @DisplayName("서로 다른 상품을 같은 이름으로 동시에 수정해도 하나만 바뀐다")
    void updateSameNameConcurrently() throws Exception {
        for (int round = 0; round < ROUNDS; round++) {
            Products products = new Products();
            List<Runnable> actions = new ArrayList<>();
            for (int i = 1; i <= THREADS; i++) {
                ProductId id = new ProductId(i);
                products.add(product(i, "Name" + i));
                actions.add(() -> products.update(id, new ProductName("Same", NO_PROFANITY),
                        new ProductPrice(1), new ProductImage("https://a.com")));
            }

            long successes = runTogether(actions);

            assertThat(successes).isEqualTo(1);
        }
    }
}
