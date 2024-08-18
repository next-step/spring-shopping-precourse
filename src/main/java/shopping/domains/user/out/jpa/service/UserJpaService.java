package shopping.domains.user.out.jpa.service;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import shopping.domains.user.core.domain.dto.UserDto;
import shopping.domains.user.core.out.adapter.UserOutAdapter;
import shopping.domains.user.out.jpa.entity.JpaUser;
import shopping.domains.user.out.jpa.repository.UserRepository;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserJpaService implements UserOutAdapter {
    private final UserRepository userRepository;

    @Override
    public @NonNull UserDto save(@NonNull final UserDto dto) {
        final JpaUser jpaUser = new JpaUser(dto);
        return userRepository.save(jpaUser).toDto();
    }

    @Override
    public @NonNull Optional<UserDto> findUser(@NonNull final UUID id) {
        return userRepository.findById(id).map(JpaUser::toDto);
    }

    @Override
    public @NonNull Optional<UserDto> findUser(final String encryptedEmail) {
        return userRepository.findByUser_Email_Value(encryptedEmail).map(JpaUser::toDto);
    }
}
