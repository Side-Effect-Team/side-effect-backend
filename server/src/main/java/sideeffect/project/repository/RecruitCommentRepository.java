package sideeffect.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sideeffect.project.domain.comment.RecruitComment;

public interface RecruitCommentRepository extends JpaRepository<RecruitComment, Long> {
}
