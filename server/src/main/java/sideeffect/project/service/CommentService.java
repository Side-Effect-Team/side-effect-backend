package sideeffect.project.service;

import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
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

    public CommentResponse registerComment(CommentRequest request, User user) {
        FreeBoard freeBoard = findFreeBoard(request);
        Comment comment = request.toComment();
        comment.associate(user, freeBoard);
        return CommentResponse.of(commentRepository.save(comment));
    }

    public FreeBoardCommentsResponse findBoardComments(Long boardId) {
        return FreeBoardCommentsResponse
            .of(CommentResponse.listOf(commentRepository.findAllByFreeBoardIdOrderByIdDesc(boardId)));
    }

    public void update(Long userId, Long commentId, CommentRequest request) {
        Comment comment = findComment(commentId);
        validateOwner(userId, comment.getUser().getId());
        comment.update(request.toComment().getContent());
    }

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
        return freeBoardRepository.findById(request.getFreeBoardId())
            .orElseThrow(() -> new EntityNotFoundException(ErrorCode.FREE_BOARD_NOT_FOUND));
    }

    private Comment findComment(Long commentId) {
        return commentRepository.findById(commentId)
            .orElseThrow(() -> new EntityNotFoundException(ErrorCode.COMMENT_NOT_FOUND));
    }
}
