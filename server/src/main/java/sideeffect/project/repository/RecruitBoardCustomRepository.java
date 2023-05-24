package sideeffect.project.repository;

import sideeffect.project.domain.stack.StackType;
import sideeffect.project.dto.recruit.RecruitBoardAndLikeDto;

import java.util.List;

public interface RecruitBoardCustomRepository {

    List<RecruitBoardAndLikeDto> findWithSearchConditions(Long userId, Long lastId, String keyword, List<StackType> stackTypes, Integer size);

    boolean existsApplicantByRecruitBoard(Long boardId, Long userId);
}
