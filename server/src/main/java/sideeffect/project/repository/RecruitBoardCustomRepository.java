package sideeffect.project.repository;

import org.springframework.data.domain.Pageable;
import sideeffect.project.domain.recruit.RecruitBoard;
import sideeffect.project.domain.stack.StackType;

import java.util.List;

public interface RecruitBoardCustomRepository {
    List<RecruitBoard> findWithSearchConditions(Long lastId, String keyword, List<StackType> stackTypes, Pageable pageable);
}
