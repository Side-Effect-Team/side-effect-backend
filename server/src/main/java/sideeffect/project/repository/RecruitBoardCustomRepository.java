package sideeffect.project.repository;

import sideeffect.project.domain.stack.StackType;
import sideeffect.project.dto.recruit.RecruitBoardAndLikeDto;

import java.util.List;
import java.util.Optional;

public interface RecruitBoardCustomRepository {

    List<RecruitBoardAndLikeDto> findWithSearchConditions(Long userId, Long lastId, String keyword, List<StackType> stackTypes, Integer size);

    Optional<RecruitBoardAndLikeDto> findByBoardIdAndUserId(Long boardId, Long userId);

    List<RecruitBoardAndLikeDto> findByAllWithLike(Long userId);
    boolean existsApplicantByRecruitBoard(Long boardId, Long userId);
}
