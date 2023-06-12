package sideeffect.project.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import sideeffect.project.domain.freeboard.FreeBoard;
import sideeffect.project.repository.freeboard.FreeBoardRepositoryCustom;

public interface FreeBoardRepository extends JpaRepository<FreeBoard, Long>, FreeBoardRepositoryCustom {

    boolean existsByProjectUrl(String projectUrl);

    @Query("SELECT distinct b from FreeBoard b "
        + "left outer join fetch b.likes "
        + "where b.id = :boardId ")
    Optional<FreeBoard> searchBoardFetchJoin(@Param("boardId") Long boardId);
}
