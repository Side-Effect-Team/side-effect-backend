package sideeffect.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import sideeffect.project.domain.user.ProviderType;
import sideeffect.project.domain.user.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long>{
    Optional<User> findByEmail(String email);
    Optional<User> findByNickname(String nickname);
    @Query("SELECT u FROM User u " +
            "WHERE u.email = :email " +
            "AND u.providerType = :providerType")
    Optional<User> findByEmailAndProvider(@Param("email") String email, @Param("providerType") ProviderType providerType);
}
