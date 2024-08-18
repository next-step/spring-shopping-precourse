package shopping.domains.user.core.out.adapter;

import lombok.NonNull;
import shopping.domains.user.core.domain.dto.TokenDto;

public interface TokenOutAdapter {
    @NonNull TokenDto save(@NonNull final TokenDto dto);
}
