package sideeffect.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sideeffect.project.domain.like.RecruitLike;

import java.util.Optional;

public interface RecruitLikeRepository extends JpaRepository<RecruitLike, Long> {
    Optional<RecruitLike> findByUserIdAndRecruitBoardId(Long userId, Long recruitBoardId);
}
