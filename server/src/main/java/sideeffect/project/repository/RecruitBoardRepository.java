package sideeffect.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import sideeffect.project.domain.recruit.RecruitBoard;

public interface RecruitBoardRepository extends JpaRepository<RecruitBoard, Long>, RecruitBoardCustomRepository {

    @Query("SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END " +
            "FROM RecruitBoard rb " +
            "JOIN rb.boardPositions bp " +
            "JOIN bp.applicants a " +
            "WHERE rb.id = :boardId " +
            "AND a.user.id = :userId")
    boolean  existsApplicantByRecruitBoard(@Param("boardId") Long boardId, @Param("userId") Long userId);

}
