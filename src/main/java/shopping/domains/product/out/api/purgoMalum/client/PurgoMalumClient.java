package shopping.domains.product.out.api.purgoMalum.client;

import lombok.NonNull;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
        name = "purgoMalumClient",
        configuration = PurgoMalumConfig.class
)
public interface PurgoMalumClient {
    @GetMapping("/containsprofanity")
    boolean containsProfanity(@RequestParam("text") @NonNull final String text);
}