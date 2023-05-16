package sideeffect.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sideeffect.project.domain.applicant.Applicant;

public interface ApplicantRepository extends JpaRepository<Applicant, Long> {

}
