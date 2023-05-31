package sideeffect.project.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sideeffect.project.common.exception.AuthException;
import sideeffect.project.common.exception.EntityNotFoundException;
import sideeffect.project.common.exception.ErrorCode;
import sideeffect.project.domain.comment.RecruitComment;
import sideeffect.project.domain.recruit.RecruitBoard;
import sideeffect.project.domain.user.User;
import sideeffect.project.dto.comment.RecruitCommentRequest;
import sideeffect.project.dto.comment.RecruitCommentResponse;
import sideeffect.project.repository.RecruitBoardRepository;
import sideeffect.project.repository.RecruitCommentRepository;

@Service
@RequiredArgsConstructor
public class RecruitCommentService {

    private final RecruitCommentRepository recruitCommentRepository;
    private final RecruitBoardRepository recruitBoardRepository;

    @Transactional
    public RecruitCommentResponse registerComment(RecruitCommentRequest request, User user) {
        RecruitBoard findRecruitBoard = recruitBoardRepository.findById(request.getBoardId())
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.RECRUIT_BOARD_NOT_FOUND));
        RecruitComment recruitComment = request.toComment();
        recruitComment.associate(user, findRecruitBoard);

        return RecruitCommentResponse.of(recruitCommentRepository.save(recruitComment));
    }

    @Transactional
    public void update(Long userId, Long commentId, String updateComment) {
        RecruitComment findRecruitComment = recruitCommentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.RECRUIT_COMMENT_NOT_FOUND));
        validateOwner(userId, findRecruitComment.getUser().getId());
        findRecruitComment.update(updateComment);
    }

    @Transactional
    public void delete(Long userId, Long commentId) {
        RecruitComment findRecruitComment = recruitCommentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.RECRUIT_COMMENT_NOT_FOUND));
        validateOwner(userId, findRecruitComment.getUser().getId());
        recruitCommentRepository.delete(findRecruitComment);
    }

    private void validateOwner(Long userId, Long ownerId) {
        if(!userId.equals(ownerId)) {
            throw new AuthException(ErrorCode.RECRUIT_COMMENT_UNAUTHORIZED);
        }
    }

}
