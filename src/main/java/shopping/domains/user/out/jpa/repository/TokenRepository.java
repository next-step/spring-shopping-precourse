package shopping.domains.user.out.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import shopping.domains.user.out.jpa.entity.JpaAuthToken;

import java.util.UUID;

@Repository
public interface TokenRepository extends JpaRepository<JpaAuthToken, UUID> {

}
