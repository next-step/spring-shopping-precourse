package shopping.domains.user.core.domain.service;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import shopping.domains.common.core.domain.entity.ResourceNotFoundException;
import shopping.domains.common.core.domain.enums.CommonErrorCode;
import shopping.domains.user.core.in.command.SignInCommand;
import shopping.domains.user.core.in.command.SignUpCommand;
import shopping.domains.user.core.domain.dto.TokenDto;
import shopping.domains.user.core.domain.dto.UserDto;
import shopping.domains.user.core.domain.entity.*;
import shopping.domains.user.core.in.adapter.UserInAdapter;
import shopping.domains.user.core.out.adapter.TokenOutAdapter;
import shopping.domains.user.core.out.adapter.UserOutAdapter;

@Service
@Slf4j
public class UserService implements UserInAdapter {
    private final UserOutAdapter userOutAdapter;

    private final TokenOutAdapter tokenOutAdapter;

    private final EncryptStrategy encryptStrategy;

    private final HashStrategy hashStrategy;

    private final TokenGenerator accessTokenGenerator;

    private final TokenGenerator refreshTokenGenerator;

    public UserService(
            @NonNull final UserOutAdapter userOutAdapter,
            @NonNull final TokenOutAdapter tokenOutAdapter,
            @NonNull final EncryptStrategy encryptStrategy,
            @NonNull final HashStrategy hashStrategy,
            @Qualifier("accessTokenGenerator") @NonNull final TokenGenerator accessTokenGenerator,
            @Qualifier("refreshTokenGenerator") @NonNull final TokenGenerator refreshTokenGenerator
    ) {
        this.userOutAdapter = userOutAdapter;
        this.tokenOutAdapter = tokenOutAdapter;
        this.encryptStrategy = encryptStrategy;
        this.hashStrategy = hashStrategy;
        this.accessTokenGenerator = accessTokenGenerator;
        this.refreshTokenGenerator = refreshTokenGenerator;
    }

    @Override
    public @NonNull TokenDto signUp(@NonNull final SignUpCommand signUpCommand) {
        final RawPassword rawPassword = new RawPassword(signUpCommand.rawPassword());
        final EncryptedEmail encryptedEmail = EncryptedEmail.encryptBuilder()
                .rawEmail(new RawEmail(signUpCommand.rawEmail()))
                .encryptStrategy(encryptStrategy)
                .build();
        final EncryptedPassword encryptedPassword = EncryptedPassword.encryptBuilder()
                .rawPassword(rawPassword)
                .hashStrategy(hashStrategy)
                .build();
        final User user = User.builder()
                .email(encryptedEmail)
                .password(encryptedPassword)
                .build();
        final UserDto savedUserDto = userOutAdapter.save(user.toDto());
        return saveToken(savedUserDto, user, rawPassword);
    }

    @Override
    public @NonNull TokenDto signIn(@NonNull final SignInCommand signInCommand) {
        final UserDto userDto = userOutAdapter.findUser(encryptStrategy.encrypt(signInCommand.rawEmail()))
                .orElseThrow(() -> new ResourceNotFoundException(CommonErrorCode.NOT_EXIST));
        final User user = new User(userDto);
        return saveToken(userDto, user, new RawPassword(signInCommand.rawPassword()));
    }

    @NonNull
    private TokenDto saveToken(
            @NonNull final UserDto userDto,
            @NonNull final User user,
            @NonNull final RawPassword rawPassword
    ) {
        final AuthToken authToken = AuthToken.builder()
                .accessToken(accessTokenGenerator.createToken(userDto.getId()))
                .refreshToken(refreshTokenGenerator.createToken(userDto.getId()))
                .build();
        final AuthToken validAuthToken = user.signIn(rawPassword, hashStrategy, authToken);
        return tokenOutAdapter.save(validAuthToken.toDto());
    }
}
