package sideeffect.project.repository;

import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import sideeffect.project.domain.freeboard.FreeBoard;
import sideeffect.project.repository.freeboard.FreeBoardRepositoryCustom;

public interface FreeBoardRepository extends JpaRepository<FreeBoard, Long>, FreeBoardRepositoryCustom {
    @Query("SELECT b from FreeBoard b order by b.likes.size desc")
    List<FreeBoard> findRankFreeBoard(Pageable pageable);

    boolean existsByProjectUrl(String projectUrl);
}
