package shopping.apps.shopping.api.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import shopping.apps.shopping.api.common.ApiUrls;
import shopping.apps.shopping.api.user.docs.UserApiDocs;
import shopping.apps.shopping.api.user.request.LoginRequest;
import shopping.apps.shopping.api.user.request.RegisterRequest;
import shopping.apps.shopping.api.user.response.TokenResponse;
import shopping.domains.user.core.domain.dto.TokenDto;
import shopping.domains.user.core.in.adapter.UserInAdapter;

@Tag(name = "user-controller", description = "사용자 컨트롤러")
@RestController
@RequestMapping(ApiUrls.User.PREFIX)
@RequiredArgsConstructor
public class UserController {
    private final UserInAdapter userInAdapter;

    @Operation(summary = UserApiDocs.Register.SUMMARY, description = UserApiDocs.Register.DESCRIPTION)
    @PostMapping(ApiUrls.User.REGISTER)
    public ResponseEntity<TokenResponse> register(
            @Valid
            @RequestBody
            final RegisterRequest request
    ) {
        final TokenDto tokenDto = userInAdapter.signUp(request.toCommand());
        return ResponseEntity.status(HttpStatus.CREATED).body(new TokenResponse(tokenDto));
    }

    @Operation(summary = UserApiDocs.Login.SUMMARY, description = UserApiDocs.Login.DESCRIPTION)
    @PostMapping(ApiUrls.User.LOGIN)
    public ResponseEntity<TokenResponse> login(
            @Valid
            @RequestBody
            final LoginRequest request
    ) {
        final TokenDto tokenDto = userInAdapter.signIn(request.toCommand());
        return ResponseEntity.status(HttpStatus.OK).body(new TokenResponse(tokenDto));
    }
}
