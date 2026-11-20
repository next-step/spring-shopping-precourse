package shopping.service;

import java.util.List;

import org.springframework.stereotype.Service;

import shopping.domain.Wish;
import shopping.exception.NotFoundException;
import shopping.exception.UnauthorizedException;
import shopping.repository.TokenRepository;
import shopping.repository.WishRepository;

@Service
public class WishService {

    private final WishRepository wishRepository;
    private final TokenRepository tokenRepository;

    public WishService(WishRepository wishRepository, TokenRepository tokenRepository) {
        this.wishRepository = wishRepository;
        this.tokenRepository = tokenRepository;
    }

    public Wish addWish(String token, Long productId) {
        Long memberId = tokenRepository.findMemberIdByToken(token);

        if (memberId == null) {
            throw new UnauthorizedException("유효하지 않은 토큰입니다.");
        }

        Wish wish = new Wish(null, memberId, productId);

        return wishRepository.save(wish);
    }

    public List<Wish> findWishes(String token) {
        Long memberId = tokenRepository.findMemberIdByToken(token);

        if (memberId == null) {
            throw new UnauthorizedException("유효하지 않은 토큰입니다.");
        }

        return wishRepository.findAllByMemberId(memberId);
    }

    public void deleteWish(String token, Long wishId) {
        Long memberId = tokenRepository.findMemberIdByToken(token);

        if (memberId == null) {
            throw new UnauthorizedException("유효하지 않은 토큰입니다.");
        }

        Wish wish = wishRepository.findById(wishId);

        if (wish == null) {
            throw new NotFoundException("위시를 찾을 수 없습니다.");
        }

        if (!wish.isOwnedBy(memberId)) {
            throw new IllegalArgumentException("본인의 위시만 삭제할 수 있습니다.");
        }

        wishRepository.deleteById(wishId);
    }
}
