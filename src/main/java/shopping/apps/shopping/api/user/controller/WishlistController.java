package shopping.apps.shopping.api.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import shopping.apps.shopping.api.common.ApiUrls;
import shopping.apps.shopping.api.user.docs.WishlistApiDocs;
import shopping.apps.shopping.api.user.request.WriteWishlistRequest;
import shopping.apps.shopping.api.user.response.WishlistInfoResponse;
import shopping.apps.shopping.api.user.response.WishlistInfoResponses;
import shopping.apps.shopping.security.bean.token.CustomUserDetails;
import shopping.domains.user.core.domain.dto.WishlistDto;
import shopping.domains.user.core.domain.entity.EncryptStrategy;
import shopping.domains.user.core.in.adapter.WishlistInAdapter;

import java.util.List;
import java.util.UUID;

@Tag(name = "wishlist-controller", description = "위시리스트 컨트롤러")
@RestController
@RequestMapping(ApiUrls.Wishlist.PREFIX)
@RequiredArgsConstructor
public class WishlistController {
    private final WishlistInAdapter wishlistInAdapter;

    private final EncryptStrategy encryptStrategy;

    @Operation(summary = WishlistApiDocs.Create.SUMMARY, description = WishlistApiDocs.Create.DESCRIPTION)
    @PostMapping(ApiUrls.Wishlist.CREATE)
    public ResponseEntity<WishlistInfoResponse> createWishlist(
            @AuthenticationPrincipal @NonNull final CustomUserDetails customUserDetails,
            @Valid @RequestBody final WriteWishlistRequest request
    ) {
        final UUID userId = customUserDetails.id();
        final WishlistDto productDto = wishlistInAdapter.createWishlist(request.toCreateCommand(userId));
        return ResponseEntity.status(HttpStatus.CREATED).body(new WishlistInfoResponse(productDto, encryptStrategy));
    }

    @Operation(summary = WishlistApiDocs.Delete.SUMMARY, description = WishlistApiDocs.Delete.DESCRIPTION)
    @DeleteMapping(ApiUrls.Wishlist.Delete.FULL_URI)
    public ResponseEntity<Void> deleteWishlist(
            @PathVariable(ApiUrls.Wishlist.Delete.WISHLIST_ID_PATH_VAR) @NonNull final UUID wishlistId
    ) {
        wishlistInAdapter.deleteWishlist(wishlistId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @Operation(summary = WishlistApiDocs.GetAllInfo.SUMMARY, description = WishlistApiDocs.GetAllInfo.DESCRIPTION)
    @GetMapping(ApiUrls.Wishlist.GET_ALL_INFO)
    public ResponseEntity<WishlistInfoResponses> getAllWishlistInfo(@AuthenticationPrincipal @NonNull final CustomUserDetails customUserDetails) {
        final UUID userId = customUserDetails.id();
        final List<WishlistDto> wishlistDtos = wishlistInAdapter.getAllWishlist(userId);
        return ResponseEntity.status(HttpStatus.OK).body(new WishlistInfoResponses(wishlistDtos, encryptStrategy));
    }
}
