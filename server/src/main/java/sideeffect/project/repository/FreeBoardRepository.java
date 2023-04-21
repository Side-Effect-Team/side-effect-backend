package sideeffect.project.repository;

import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import sideeffect.project.domain.freeboard.FreeBoard;

public interface FreeBoardRepository extends JpaRepository<FreeBoard, Long> {

    List<FreeBoard> findAllByContentContainingOrTitleContaining(String content, String title);

    List<FreeBoard> findByIdLessThanOrderByIdDesc(Long boardId, Pageable pageable);
}
