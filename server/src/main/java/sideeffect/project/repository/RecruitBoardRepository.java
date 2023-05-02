package sideeffect.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sideeffect.project.domain.recruit.RecruitBoard;

public interface RecruitBoardRepository extends JpaRepository<RecruitBoard, Long>, RecruitBoardCustomRepository {
}
