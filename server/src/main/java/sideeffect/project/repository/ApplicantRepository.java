package sideeffect.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import sideeffect.project.domain.applicant.Applicant;

import java.util.Optional;

public interface ApplicantRepository extends JpaRepository<Applicant, Long> {

    @Query("SELECT a " +
            "FROM Applicant a " +
            "WHERE a.boardPosition.id = :boardPositionId AND a.user.id = :userId")
    Optional<Applicant> isUserApplicantForBoardPosition(@Param("userId") Long userId, @Param("boardPositionId") Long boardPositionId);

}
