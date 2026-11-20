package shopping.service;

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
import shopping.domain.Product;
import shopping.domain.ProductId;
import shopping.domain.Products;

class ProductServiceConcurrencyTest {
    private static final String URL = "https://example.com/a.png";
    private static final int THREADS = 8;
    private static final int ROUNDS = 200;

    private static ProductService newService() {
        return new ProductService(new Products(), text -> false);
    }

    private static List<Product> createTogether(ProductService service, List<String> names)
            throws Exception {
        ExecutorService executor = Executors.newFixedThreadPool(names.size());
        CountDownLatch start = new CountDownLatch(1);
        List<Future<Product>> futures = new ArrayList<>();
        for (String name : names) {
            futures.add(executor.submit(() -> createWhenStarted(service, start, name)));
        }
        start.countDown();
        List<Product> created = collect(futures);
        executor.shutdownNow();
        return created;
    }

    private static Product createWhenStarted(ProductService service, CountDownLatch start,
            String name) throws InterruptedException {
        start.await();
        try {
            return service.create(name, 1000, URL);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private static List<Product> collect(List<Future<Product>> futures) throws Exception {
        List<Product> created = new ArrayList<>();
        for (Future<Product> future : futures) {
            Product product = future.get(10, TimeUnit.SECONDS);
            if (product != null) {
                created.add(product);
            }
        }
        return created;
    }

    @Test
    @DisplayName("서로 다른 이름의 상품을 동시에 만들어도 ID가 겹치지 않고 빠짐없이 부여된다")
    void createDifferentNamesConcurrently() throws Exception {
        for (int round = 0; round < ROUNDS; round++) {
            ProductService service = newService();
            List<String> names = new ArrayList<>();
            for (int i = 1; i <= THREADS; i++) {
                names.add("Name" + i);
            }

            List<Product> created = createTogether(service, names);

            List<Long> ids = created.stream().map(p -> p.id().orElseThrow().value()).sorted()
                    .toList();
            assertThat(ids).containsExactly(1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L);
            assertThat(service.list()).hasSize(THREADS);
        }
    }

    @Test
    @DisplayName("같은 이름의 상품을 동시에 만들면 하나만 생기고 ID는 건너뛰지 않는다")
    void createSameNameConcurrently() throws Exception {
        for (int round = 0; round < ROUNDS; round++) {
            ProductService service = newService();
            List<String> names = new ArrayList<>();
            for (int i = 1; i <= THREADS; i++) {
                names.add("Same");
            }

            List<Product> created = createTogether(service, names);

            assertThat(created).hasSize(1);
            assertThat(service.create("Other", 1, URL).id()).contains(new ProductId(2));
        }
    }
}
