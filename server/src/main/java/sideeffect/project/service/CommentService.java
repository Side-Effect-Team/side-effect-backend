package sideeffect.project.service;

import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sideeffect.project.common.exception.AuthException;
import sideeffect.project.common.exception.EntityNotFoundException;
import sideeffect.project.common.exception.ErrorCode;
import sideeffect.project.domain.comment.Comment;
import sideeffect.project.domain.freeboard.FreeBoard;
import sideeffect.project.domain.user.User;
import sideeffect.project.dto.comment.CommentRequest;
import sideeffect.project.dto.comment.CommentResponse;
import sideeffect.project.dto.comment.FreeBoardCommentsResponse;
import sideeffect.project.repository.CommentRepository;
import sideeffect.project.repository.FreeBoardRepository;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final FreeBoardRepository freeBoardRepository;

    @Transactional
    public CommentResponse registerComment(CommentRequest request, User user) {
        FreeBoard freeBoard = findFreeBoard(request);
        Comment comment = request.toComment();
        comment.associate(user, freeBoard);
        return CommentResponse.of(commentRepository.save(comment));
    }

    @Transactional
    public FreeBoardCommentsResponse findBoardComments(Long boardId) {
        return FreeBoardCommentsResponse
            .of(CommentResponse.listOf(commentRepository.findAllByFreeBoardIdOrderByIdDesc(boardId)));
    }

    @Transactional
    public void update(Long userId, Long commentId, String updateComment) {
        Comment comment = findComment(commentId);
        validateOwner(userId, comment.getUser().getId());
        comment.update(updateComment);
    }

    @Transactional
    public void delete(Long userId, Long commentId) {
        Comment comment = findComment(commentId);
        validateOwner(userId, comment.getUser().getId());
        commentRepository.delete(comment);
    }

    private void validateOwner(Long userId, Long ownerId) {
        if (!Objects.equals(userId, ownerId)) {
            throw new AuthException(ErrorCode.COMMENT_UNAUTHORIZED);
        }
    }

    private FreeBoard findFreeBoard(CommentRequest request) {
        return freeBoardRepository.findById(request.getBoardId())
            .orElseThrow(() -> new EntityNotFoundException(ErrorCode.FREE_BOARD_NOT_FOUND));
    }

    private Comment findComment(Long commentId) {
        return commentRepository.findById(commentId)
            .orElseThrow(() -> new EntityNotFoundException(ErrorCode.COMMENT_NOT_FOUND));
    }
}
