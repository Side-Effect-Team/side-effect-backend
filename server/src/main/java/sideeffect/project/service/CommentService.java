package sideeffect.project.service;

import java.util.Objects;
import javax.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sideeffect.project.domain.comment.Comment;
import sideeffect.project.domain.freeboard.FreeBoard;
import sideeffect.project.domain.user.User;
import sideeffect.project.dto.comment.CommentRequest;
import sideeffect.project.dto.comment.CommentResponse;
import sideeffect.project.dto.comment.FreeBoardCommentsResponse;
import sideeffect.project.repository.CommentRepository;
import sideeffect.project.repository.FreeBoardRepository;
import sideeffect.project.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final FreeBoardRepository freeBoardRepository;

    public CommentResponse registerComment(CommentRequest request) {
        User user = userRepository.findById(request.getUserId()).orElseThrow(EntityNotFoundException::new);
        FreeBoard freeBoard = freeBoardRepository.findById(request.getFreeBoardId())
            .orElseThrow(EntityNotFoundException::new);
        Comment comment = request.toComment();
        comment.associate(user, freeBoard);
        return CommentResponse.of(commentRepository.save(comment));
    }

    public FreeBoardCommentsResponse findBoardComments(Long boardId) {
        return FreeBoardCommentsResponse
            .of(CommentResponse.listOf(commentRepository.findAllByFreeBoardIdOrderByIdDesc(boardId)));
    }

    public void update(Long userId, Long commentId, CommentRequest request) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(EntityNotFoundException::new);
        validateOwner(userId, comment.getUser().getId());
        comment.update(request.toComment().getContent());
    }

    public void delete(Long userId, Long commentId) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(EntityNotFoundException::new);
        validateOwner(userId, comment.getUser().getId());
        commentRepository.delete(comment);
    }

    private void validateOwner(Long userId, Long ownerId) {
        if (!Objects.equals(userId, ownerId)) {
            throw new IllegalArgumentException("댓글 작성자가 아닙니다.");
        }
    }
}
