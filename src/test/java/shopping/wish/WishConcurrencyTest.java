package shopping.wish;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import shopping.auth.JwtProvider;
import shopping.member.entity.Member;
import shopping.member.repository.MemberRepository;
import shopping.product.entity.Product;
import shopping.product.repository.ProductRepository;
import shopping.wish.repository.WishRepository;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureMockMvc
class WishConcurrencyTest {

    @Autowired
    MockMvcTester mvc;

    @Autowired
    JwtProvider jwtProvider;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    WishRepository wishRepository;

    @AfterEach
    void tearDown() {
        wishRepository.deleteAll();
        productRepository.deleteAll();
        memberRepository.deleteAll();
    }

    @Test
    void 같은_상품을_동시에_두_번_담으면_하나는_201_하나는_409다() throws Exception {
        // given
        Member member = memberRepository.save(new Member("race@example.com", "hash", "동시"));
        Product product = productRepository.save(
                new Product("아메리카노", 4000, "https://example.com/a.png"));
        String token = jwtProvider.createToken(member.getId());
        String body = "{\"productId\": %d}".formatted(product.getId());

        // when
        List<Integer> statuses = sendTwiceAtOnce(() -> addWishStatus(token, body));

        // then
        assertThat(statuses).containsExactlyInAnyOrder(201, 409);
        assertThat(wishRepository.count()).isEqualTo(1);
    }

    List<Integer> sendTwiceAtOnce(Callable<Integer> request) throws Exception {
        CyclicBarrier barrier = new CyclicBarrier(2);
        Callable<Integer> task = () -> {
            barrier.await();
            return request.call();
        };

        ExecutorService executor = Executors.newFixedThreadPool(2);

        try {
            List<Future<Integer>> futures = executor.invokeAll(List.of(task, task));

            return List.of(futures.get(0).get(), futures.get(1).get());
        } finally {
            executor.shutdown();
        }
    }

    int addWishStatus(String token, String body) {
        return mvc.post().uri("/api/wishes")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
                .exchange()
                .getResponse()
                .getStatus();
    }
}
