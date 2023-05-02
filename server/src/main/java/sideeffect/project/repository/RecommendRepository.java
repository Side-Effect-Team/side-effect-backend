package sideeffect.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sideeffect.project.domain.recommend.Recommend;

public interface RecommendRepository extends JpaRepository<Recommend, Long> {

    boolean existsByUserIdAndFreeBoardId(Long userId, Long freeBoardId);
}
