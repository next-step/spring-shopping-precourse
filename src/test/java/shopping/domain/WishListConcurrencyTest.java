package shopping.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class WishListConcurrencyTest {
    private static final ProfanityChecker NO_PROFANITY = text -> false;

    private static Product product(long id, String name) {
        return new Product(new ProductId(id), new ProductName(name, NO_PROFANITY),
                new ProductPrice(1000), new ProductImage("https://example.com/a.png"));
    }

    private static long runTogether(List<Runnable> actions) throws Exception {
        ExecutorService executor = Executors.newFixedThreadPool(actions.size());
        CountDownLatch start = new CountDownLatch(1);
        List<Future<Boolean>> results = new ArrayList<>();
        for (Runnable action : actions) {
            results.add(executor.submit(() -> succeedsWhenStarted(start, action)));
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

    private static boolean succeedsWhenStarted(CountDownLatch start, Runnable action)
            throws InterruptedException {
        start.await();
        try {
            action.run();
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    @Test
    @DisplayName("같은 상품을 동시에 추가해도 하나만 담긴다")
    void addSameProductConcurrently() throws Exception {
        for (int round = 0; round < 300; round++) {
            WishList wishList = new WishList();
            Product product = product(1, "Same");
            List<Runnable> actions = new ArrayList<>();
            for (int i = 0; i < 8; i++) {
                actions.add(() -> wishList.add(product));
            }

            long successes = runTogether(actions);

            assertThat(successes).isEqualTo(1);
            assertThat(wishList.all()).hasSize(1);
        }
    }

    @Test
    @DisplayName("서로 다른 상품은 동시에 추가해도 모두 담긴다")
    void addDifferentProductsConcurrently() throws Exception {
        int count = 400;
        WishList wishList = new WishList();
        List<Runnable> actions = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            Product product = product(i, "Name" + i);
            actions.add(() -> wishList.add(product));
        }

        long successes = runTogether(actions);

        assertThat(successes).isEqualTo(count);
        assertThat(wishList.all()).hasSize(count);
    }
}
