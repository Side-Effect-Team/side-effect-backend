package sideeffect.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sideeffect.project.domain.user.UserPosition;

public interface UserPositionRepository extends JpaRepository<UserPosition, Long> {
}
