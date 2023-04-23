package sideeffect.project.repository;

import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import sideeffect.project.domain.freeboard.FreeBoard;

public interface FreeBoardRepository extends JpaRepository<FreeBoard, Long> {

    List<FreeBoard> findAllByContentContainingOrTitleContaining(String content, String title);

    @Query("SELECT b from FreeBoard b order by b.id desc")
    List<FreeBoard> findLastPagingBoards(Pageable pageable);

    List<FreeBoard> findByIdLessThanOrderByIdDesc(Long boardId, Pageable pageable);
}
