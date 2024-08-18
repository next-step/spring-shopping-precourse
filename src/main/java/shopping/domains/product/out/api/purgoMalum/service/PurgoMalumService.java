package shopping.domains.product.out.api.purgoMalum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import shopping.domains.common.core.domain.entity.ThirdPartyUnavailableException;
import shopping.domains.common.core.domain.enums.CommonErrorCode;
import shopping.domains.product.core.out.adapter.ProfanityFilterOutAdapter;
import shopping.domains.product.out.api.purgoMalum.client.PurgoMalumClient;

@Service
@RequiredArgsConstructor
@Slf4j
public class PurgoMalumService implements ProfanityFilterOutAdapter {
    private final PurgoMalumClient purgoMalumClient;

    @Override
    @NonNull
    public boolean isCleanText(@NonNull final String value) {
        return !isNotCleanText(value);
    }

    @Override
    @NonNull
    public boolean isNotCleanText(@NonNull final String value) {
        try {
            return purgoMalumClient.containsProfanity(value);
        } catch(final Exception e) {
            throw new ThirdPartyUnavailableException(CommonErrorCode.SERVICE_UNAVAILABLE);
        }
    }
}