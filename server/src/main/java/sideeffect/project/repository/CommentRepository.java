package sideeffect.project.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import sideeffect.project.domain.comment.Comment;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findAllByFreeBoardIdOrderByIdDesc(Long freeBoardId);
}
