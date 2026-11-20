package shopping;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(
        properties = {
            "spring.datasource.url=jdbc:h2:mem:wish-concurrency;DB_CLOSE_DELAY=-1",
            "spring.datasource.driver-class-name=org.h2.Driver",
            "spring.datasource.username=sa",
            "spring.datasource.password=",
            "spring.jpa.hibernate.ddl-auto=create-drop",
            "spring.flyway.enabled=false"
        })
class WishConcurrencyTest {

    @Autowired private WishService service;

    @Autowired private ProductService productService;

    @Autowired private WishRepository wishes;

    @Autowired private MemberRepository members;

    @Autowired private ProductRepository products;

    private Long memberId;
    private Long productId;

    @BeforeEach
    void setUp() {
        memberId = members.saveAndFlush(new Member("race@example.com", "hash")).getId();
        productId =
                products.saveAndFlush(new Product("keyboard", 1000, "https://example.com/a.jpg"))
                        .getId();
    }

    @AfterEach
    void cleanUp() {
        wishes.deleteAllInBatch();
        products.deleteAllInBatch();
        members.deleteAllInBatch();
    }

    @Test
    void concurrentDuplicateRequestsCreateOnlyOneWish() throws Exception {
        assertThat(race(this::addWish, this::addWish))
                .containsExactlyInAnyOrder("added", "duplicate");
        assertThat(wishes.count()).isEqualTo(1);
    }

    @Test
    void concurrentProductDeletionLeavesNoWishOrProduct() throws Exception {
        List<String> results = race(this::addWish, this::deleteProduct);
        assertThat(results).contains("deleted");
        assertThat(results.getFirst()).isIn("added", "missing");
        assertThat(products.existsById(productId)).isFalse();
        assertThat(wishes.count()).isZero();
    }

    private String addWish() {
        try {
            service.add(memberId, productId);
            return "added";
        } catch (DuplicateWishException exception) {
            return "duplicate";
        } catch (ProductNotFoundException exception) {
            return "missing";
        }
    }

    private String deleteProduct() {
        productService.delete(productId);
        return "deleted";
    }

    private List<String> race(Callable<String> first, Callable<String> second) throws Exception {
        CountDownLatch start = new CountDownLatch(1);
        try (ExecutorService executor = Executors.newFixedThreadPool(2)) {
            Future<String> firstResult = submit(executor, start, first);
            Future<String> secondResult = submit(executor, start, second);
            start.countDown();
            return List.of(
                    firstResult.get(10, TimeUnit.SECONDS), secondResult.get(10, TimeUnit.SECONDS));
        }
    }

    private Future<String> submit(
            ExecutorService executor, CountDownLatch start, Callable<String> operation) {
        return executor.submit(
                () -> {
                    start.await();
                    return operation.call();
                });
    }
}
