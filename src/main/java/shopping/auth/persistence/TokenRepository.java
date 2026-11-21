package shopping.auth.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import shopping.auth.domain.AccessToken;

public interface TokenRepository extends JpaRepository<AccessToken, String> {
}
