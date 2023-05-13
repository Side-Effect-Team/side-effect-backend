package sideeffect.project.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sideeffect.project.common.exception.EntityNotFoundException;
import sideeffect.project.common.exception.ErrorCode;
import sideeffect.project.domain.like.RecruitLike;
import sideeffect.project.domain.recruit.RecruitBoard;
import sideeffect.project.domain.user.User;
import sideeffect.project.dto.like.LikeResult;
import sideeffect.project.dto.like.RecruitLikeResponse;
import sideeffect.project.repository.RecruitBoardRepository;
import sideeffect.project.repository.RecruitLikeRepository;
import sideeffect.project.repository.UserRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RecruitLikeService {

    private final RecruitLikeRepository recruitLikeRepository;
    private final UserRepository userRepository;
    private final RecruitBoardRepository recruitBoardRepository;

    @Transactional
    public RecruitLikeResponse toggleLike(Long userId, Long boardId) {
        Optional<RecruitLike> recruitLike = recruitLikeRepository.findByUserIdAndRecruitBoardId(userId, boardId);

        if (recruitLike.isPresent()) {
            RecruitLike findRecruitLike = recruitLike.get();
            recruitLikeRepository.delete(findRecruitLike);
            return RecruitLikeResponse.of(findRecruitLike, LikeResult.CANCEL_LIKE);
        }

        return RecruitLikeResponse.of(likeBoard(userId, boardId), LikeResult.LIKE);
    }

    private RecruitLike likeBoard(Long userId, Long boardId) {
        User findUser = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.USER_NOT_FOUND));
        RecruitBoard findRecruitBoard = recruitBoardRepository.findById(boardId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.RECRUIT_BOARD_NOT_FOUND));

        return recruitLikeRepository.save(RecruitLike.createRecruitLike(findUser, findRecruitBoard));
    }


}
