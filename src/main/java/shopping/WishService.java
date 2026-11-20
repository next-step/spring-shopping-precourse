package shopping;

import java.sql.SQLException;
import java.util.List;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class WishService {

    private final WishRepository wishes;
    private final MemberRepository members;
    private final ProductRepository products;

    public WishService(
            WishRepository wishes, MemberRepository members, ProductRepository products) {
        this.wishes = wishes;
        this.members = members;
        this.products = products;
    }

    @Transactional
    public WishResponse add(Long memberId, Long productId) {
        Product product =
                products.findByIdForUpdate(productId)
                        .orElseThrow(() -> new ProductNotFoundException(productId));
        Member member = members.findById(memberId).orElseThrow(InvalidTokenException::new);
        if (wishes.existsByMember_IdAndProduct_Id(memberId, productId)) {
            throw new DuplicateWishException();
        }
        return WishResponse.from(saveWish(new Wish(member, product)));
    }

    @Transactional(readOnly = true)
    public List<WishResponse> findAll(Long memberId) {
        return wishes.findAllByMember_IdOrderByIdAsc(memberId).stream()
                .map(WishResponse::from)
                .toList();
    }

    @Transactional
    public void delete(Long memberId, Long wishId) {
        Wish wish = wishes.findById(wishId).orElseThrow(WishNotFoundException::new);
        if (!wish.isOwnedBy(memberId)) {
            throw new WishAccessDeniedException();
        }
        wishes.delete(wish);
    }

    private Wish saveWish(Wish wish) {
        try {
            return wishes.saveAndFlush(wish);
        } catch (DataIntegrityViolationException exception) {
            if (exception.getMostSpecificCause() instanceof SQLException sql
                    && (sql.getErrorCode() == 1062 || "23505".equals(sql.getSQLState()))) {
                throw new DuplicateWishException();
            }
            throw exception;
        }
    }
}
