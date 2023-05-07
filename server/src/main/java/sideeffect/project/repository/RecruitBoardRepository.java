package sideeffect.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import sideeffect.project.domain.applicant.ApplicantStatus;
import sideeffect.project.domain.recruit.RecruitBoard;
import sideeffect.project.dto.applicant.ApplicantListResponse;

import java.util.List;

public interface RecruitBoardRepository extends JpaRepository<RecruitBoard, Long>, RecruitBoardCustomRepository {

    @Query("SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END " +
            "FROM RecruitBoard rb " +
            "JOIN rb.boardPositions bp " +
            "JOIN bp.applicants a " +
            "WHERE rb.id = :boardId " +
            "AND a.user.id = :userId")
    boolean  existsApplicantByRecruitBoard(@Param("boardId") Long boardId, @Param("userId") Long userId);

    @Query("SELECT new sideeffect.project.dto.applicant.ApplicantListResponse(a.user.id, a.id, u.nickname, p.positionType, a.createAt) " +
            "FROM RecruitBoard rb " +
            "INNER JOIN rb.boardPositions bp " +
            "ON rb.id = :boardId " +
            "JOIN bp.position p " +
            "INNER JOIN bp.applicants a " +
            "ON a.status = :status " +
            "JOIN a.user u")
    List<ApplicantListResponse> getApplicantsByPosition(@Param("boardId") Long boardId, @Param("status") ApplicantStatus status);
}
