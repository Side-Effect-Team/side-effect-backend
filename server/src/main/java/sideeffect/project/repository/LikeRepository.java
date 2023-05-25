package sideeffect.project.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import sideeffect.project.domain.like.Like;

public interface LikeRepository extends JpaRepository<Like, Long> {

    @Query("SELECT l from Like l join fetch l.freeBoard where l.user.id = :userId and l.freeBoard.id = :freeBoardId")
    Optional<Like> searchLike(@Param("userId") Long userId,@Param("freeBoardId") Long freeBoardId);

    boolean existsByUserIdAndFreeBoardId(Long userId, Long boardId);
}
