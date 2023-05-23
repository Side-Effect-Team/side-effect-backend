package sideeffect.project.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import sideeffect.project.domain.penalty.Penalty;

public interface PenaltyRepository extends JpaRepository<Penalty, Long> {

    Optional<Penalty> findByUserIdAndRecruitBoardId(Long userId, Long boardId);
}
