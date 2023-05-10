package sideeffect.project.repository;

import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import sideeffect.project.domain.freeboard.FreeBoard;
import sideeffect.project.repository.freeboard.FreeBoardRepositoryCustom;

public interface FreeBoardRepository extends JpaRepository<FreeBoard, Long>, FreeBoardRepositoryCustom {

    @Query("SELECT b from FreeBoard b where b.content like %:keyword% or b.title like %:keyword% "
    + "order by b.id desc")
    List<FreeBoard> findStartScrollOfBoardsWithKeyWord(@Param("keyword") String keyWord, Pageable pageable);

    @Query("SELECT b from FreeBoard b where b.id < :id and (b.content like %:keyword% or b.title like %:keyword%) "
        + "order by b.id desc")
    List<FreeBoard> findScrollOfBoardsWithKeyWord(@Param("keyword") String keyWord, @Param("id") Long id,
        Pageable pageable);

    @Query("SELECT b from FreeBoard b order by b.id desc")
    List<FreeBoard> findStartScrollOfBoard(Pageable pageable);

    List<FreeBoard> findByIdLessThanOrderByIdDesc(Long boardId, Pageable pageable);

    @Query("SELECT b from FreeBoard b order by b.likes.size desc")
    List<FreeBoard> findRankFreeBoard(Pageable pageable);
}
