package sideeffect.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sideeffect.project.domain.recruit.BoardPosition;

public interface BoardPositionRepository extends JpaRepository<BoardPosition, Long> {
}
