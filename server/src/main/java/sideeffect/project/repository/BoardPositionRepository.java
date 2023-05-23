package sideeffect.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import sideeffect.project.domain.recruit.BoardPosition;

import java.util.Optional;

public interface BoardPositionRepository extends JpaRepository<BoardPosition, Long> {

    @Query("SELECT bp " +
            "FROM BoardPosition bp " +
            "INNER JOIN bp.applicants a " +
            "ON a.id = :applicantId " +
            "WHERE bp.currentNumber < bp.targetNumber")
    Optional<BoardPosition> findBoardPositionIfRecruitable(@Param("applicantId") Long applicantId);

    @Query("SELECT bp " +
            "FROM BoardPosition bp " +
            "INNER JOIN bp.applicants a " +
            "ON a.id = :applicantId")
    Optional<BoardPosition> findByApplicantId(@Param("applicantId") Long applicantId);

    @Query("SELECT bp FROM BoardPosition bp JOIN FETCH bp.recruitBoard WHERE bp.id = :boardPositionId")
    Optional<BoardPosition> findByIdWithRecruitBoard(@Param("boardPositionId") Long boardPositionId);

}
