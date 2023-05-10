package sideeffect.project.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import sideeffect.project.domain.like.Like;

public interface LikeRepository extends JpaRepository<Like, Long> {

    boolean existsByUserIdAndFreeBoardId(Long userId, Long freeBoardId);

    Optional<Like> findByUserIdAndFreeBoardId(Long userId, Long freeBoardId);
}
