package sideeffect.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sideeffect.project.domain.user.UserStack;

public interface UserStackRepository extends JpaRepository<UserStack, Long> {
}
