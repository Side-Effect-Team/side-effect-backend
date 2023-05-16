package sideeffect.project.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sideeffect.project.common.exception.AuthException;
import sideeffect.project.domain.comment.Comment;
import sideeffect.project.domain.freeboard.FreeBoard;
import sideeffect.project.domain.user.User;
import sideeffect.project.dto.comment.CommentRequest;
import sideeffect.project.dto.comment.FreeBoardCommentsResponse;
import sideeffect.project.repository.CommentRepository;
import sideeffect.project.repository.FreeBoardRepository;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    private CommentService commentService;

    @Mock
    private FreeBoardRepository freeBoardRepository;

    @Mock
    private CommentRepository commentRepository;

    private User user;
    private FreeBoard freeBoard;
    private Comment comment;

    @BeforeEach
    void setUp() {
        commentService = new CommentService(commentRepository, freeBoardRepository);

        user = User.builder()
            .id(1L)
            .email("test@naver.com")
            .nickname("tester")
            .password("1234")
            .build();

        freeBoard = FreeBoard.builder()
            .id(1L)
            .title("게시판입니다.")
            .projectUrl("url")
            .imgUrl("/test.jpg")
            .content("test")
            .build();

        comment = new Comment("hello");
        comment.setId(1L);
        comment.associate(user, freeBoard);
    }

    @DisplayName("댓글을 추가한다.")
    @Test
    void registerComment() {
        CommentRequest request = CommentRequest.builder()
            .boardId(freeBoard.getId()).content("hello").build();
        when(freeBoardRepository.findById(any())).thenReturn(Optional.of(freeBoard));
        when(commentRepository.save(any())).thenReturn(comment);

        commentService.registerComment(request, user);

        assertAll(
            () -> verify(freeBoardRepository).findById(any()),
            () -> verify(commentRepository).save(any())
        );
    }

    @DisplayName("댓글을 업데이트 한다.")
    @Test
    void updateComment() {
        String content = "updated";
        when(commentRepository.findById(any())).thenReturn(Optional.of(comment));

        commentService.update(user.getId(), comment.getId(), content);

        assertAll(
            () -> assertThat(comment.getContent()).isEqualTo(content),
            () -> verify(commentRepository).findById(any())
        );
    }

    @DisplayName("댓글 작성자가 아닌 다른 사람이 수정하면 예외가 발생한다.")
    @Test
    void updateCommentByNonOwner() {
        Long nonOwnerId = 2L;
        Long commentId = comment.getId();
        String content = "updated";
        when(commentRepository.findById(any())).thenReturn(Optional.of(comment));

        assertThatThrownBy(() -> commentService.update(nonOwnerId, commentId, content))
            .isInstanceOf(AuthException.class);
    }

    @DisplayName("게시판 댓글을 조회한다.")
    @Test
    void findBoardComments() {
        Comment comment1 = new Comment("댓글1");
        Comment comment2 = new Comment("댓글2");
        comment1.associate(user, freeBoard);
        comment2.associate(user, freeBoard);
        when(commentRepository.findAllByFreeBoardIdOrderByIdDesc(any())).thenReturn(List.of(comment1, comment2));

        FreeBoardCommentsResponse response = commentService.findBoardComments(freeBoard.getId());

        assertAll(
            () -> verify(commentRepository).findAllByFreeBoardIdOrderByIdDesc(any()),
            () -> assertThat(response.getCommentResponses()).hasSize(2)
        );
    }

    @DisplayName("댓글을 삭제한다.")
    @Test
    void deleteComment() {
        when(commentRepository.findById(any())).thenReturn(Optional.of(comment));

        commentService.delete(user.getId(), freeBoard.getId());

        assertAll(
            () -> verify(commentRepository).findById(any()),
            () -> verify(commentRepository).delete(any())
        );
    }

    @DisplayName("댓글 작성자가 아닌 다른 사람이 삭제하면 예외가 발생한다.")
    @Test
    void deleteCommentByNonOwner() {
        Long nonOwnerId = 2L;
        Long boardId = freeBoard.getId();
        when(commentRepository.findById(any())).thenReturn(Optional.of(comment));

        assertThatThrownBy(() -> commentService.delete(nonOwnerId, boardId))
            .isInstanceOf(AuthException.class);
    }
}
