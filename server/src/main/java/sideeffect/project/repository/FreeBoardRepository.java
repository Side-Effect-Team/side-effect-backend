package sideeffect.project.repository;

import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import sideeffect.project.domain.freeboard.FreeBoard;

public interface FreeBoardRepository extends JpaRepository<FreeBoard, Long> {

    @Query("SELECT b from FreeBoard b where b.content like %:keyword% or b.title like %:keyword% "
    + "order by b.id desc")
    List<FreeBoard> findFreeBoardWithKeyWord(@Param("keyword") String keyWord, Pageable pageable);

    @Query("SELECT b from FreeBoard b where b.id < :id and (b.content like %:keyword% or b.title like %:keyword%) "
        + "order by b.id desc")
    List<FreeBoard> findFreeBoardScrollWithKeyWord(@Param("keyword") String keyWord, @Param("id") Long id,
        Pageable pageable);

    @Query("SELECT b from FreeBoard b order by b.id desc")
    List<FreeBoard> findLastPagingBoards(Pageable pageable);

    List<FreeBoard> findByIdLessThanOrderByIdDesc(Long boardId, Pageable pageable);
}
