package shopping.domains.user.out.jpa.repository;

import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import shopping.domains.user.out.jpa.entity.JpaUser;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<JpaUser, UUID> {
    @NonNull
    Optional<JpaUser> findByUser_Email_Value(@NonNull final String email);
}
