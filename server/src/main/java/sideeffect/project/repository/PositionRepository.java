package sideeffect.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sideeffect.project.domain.position.Position;
import sideeffect.project.domain.position.PositionType;

import java.util.Optional;

public interface PositionRepository extends JpaRepository<Position, Long> {
    Optional<Position> findByPositionType(PositionType positionType);
}
