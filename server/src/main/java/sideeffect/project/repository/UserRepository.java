package sideeffect.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sideeffect.project.domain.user.User;

public interface UserRepository extends JpaRepository<User, Long> {

}
