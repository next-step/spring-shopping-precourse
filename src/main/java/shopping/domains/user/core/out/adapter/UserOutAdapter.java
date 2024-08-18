package shopping.domains.user.core.out.adapter;

import lombok.NonNull;
import shopping.domains.user.core.domain.dto.UserDto;

import java.util.Optional;
import java.util.UUID;

public interface UserOutAdapter {
    @NonNull UserDto save(@NonNull final UserDto dto);

    @NonNull Optional<UserDto> findUser(@NonNull final UUID id);

    @NonNull Optional<UserDto> findUser(final String encryptedEmail);
}
