package shopping.domains.user.out.jpa.service;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import shopping.domains.user.core.domain.dto.TokenDto;
import shopping.domains.user.core.out.adapter.TokenOutAdapter;
import shopping.domains.user.out.jpa.entity.JpaAuthToken;
import shopping.domains.user.out.jpa.repository.TokenRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class TokenJpaService implements TokenOutAdapter {
    private final TokenRepository tokenRepository;

    @Override
    public @NonNull TokenDto save(@NonNull final TokenDto dto) {
        return tokenRepository.save(new JpaAuthToken(dto)).toDto();
    }
}
