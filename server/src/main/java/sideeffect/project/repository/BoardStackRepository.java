package sideeffect.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sideeffect.project.domain.recruit.BoardStack;

public interface BoardStackRepository extends JpaRepository<BoardStack, Long> {
}
