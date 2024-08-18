package shopping.apps.shopping.security.bean.encrypt;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import shopping.domains.user.core.domain.entity.HashStrategy;

@Component
@RequiredArgsConstructor
public class BcryptHashStrategy implements HashStrategy {
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public @NonNull String encrypt(@NonNull final String origin) {
        return bCryptPasswordEncoder.encode(origin);
    }

    @Override
    public boolean match(@NonNull final String origin, @NonNull final String encrypted) {
        return bCryptPasswordEncoder.matches(origin, encrypted);
    }

}
