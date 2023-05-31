package sideeffect.project.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sideeffect.project.common.exception.AuthException;
import sideeffect.project.domain.comment.RecruitComment;
import sideeffect.project.domain.recruit.RecruitBoard;
import sideeffect.project.domain.user.User;
import sideeffect.project.dto.comment.RecruitCommentRequest;
import sideeffect.project.repository.RecruitBoardRepository;
import sideeffect.project.repository.RecruitCommentRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RecruitCommentServiceTest {

    @InjectMocks
    private RecruitCommentService recruitCommentService;

    @Mock
    private RecruitBoardRepository recruitBoardRepository;

    @Mock
    private RecruitCommentRepository recruitCommentRepository;

    private User user;
    private RecruitBoard recruitBoard;
    private RecruitComment recruitComment;

    @BeforeEach
    void setUp() {
        user = User.builder()
            .id(1L)
            .email("test@naver.com")
            .nickname("test")
            .password("1234")
            .build();

        recruitBoard = RecruitBoard.builder()
                .id(1L)
                .title("모집 게시판")
                .contents("모집 게시판 내용")
                .build();

        recruitComment = RecruitComment.builder().id(1L).content("댓글 내용").build();
        recruitComment.associate(user, recruitBoard);
    }

    @DisplayName("댓글을 추가한다.")
    @Test
    void registerComment() {
        RecruitCommentRequest request = RecruitCommentRequest.builder()
            .boardId(recruitBoard.getId()).content("hello").build();

        when(recruitBoardRepository.findById(any())).thenReturn(Optional.of(recruitBoard));
        when(recruitCommentRepository.save(any())).thenReturn(recruitComment);

        recruitCommentService.registerComment(request, user);

        assertAll(
            () -> verify(recruitBoardRepository).findById(any()),
            () -> verify(recruitCommentRepository).save(any())
        );
    }

    @DisplayName("댓글을 업데이트 한다.")
    @Test
    void updateComment() {
        String content = "수정 내용";

        when(recruitCommentRepository.findById(any())).thenReturn(Optional.of(recruitComment));

        recruitCommentService.update(user.getId(), recruitComment.getId(), content);

        assertAll(
                () -> verify(recruitCommentRepository).findById(any()),
                () -> assertThat(recruitComment.getContent()).isEqualTo(content)
        );
    }

    @DisplayName("댓글 작성자가 아닌 다른 사람이 수정하면 예외가 발생한다.")
    @Test
    void updateCommentByNonOwner() {
        Long nonOwnerId = 2L;
        Long commentId = recruitComment.getId();
        String content = "수정 내용";
        when(recruitCommentRepository.findById(any())).thenReturn(Optional.of(recruitComment));

        assertThatThrownBy(() -> recruitCommentService.update(nonOwnerId, commentId, content))
            .isInstanceOf(AuthException.class);
    }

    @DisplayName("댓글을 삭제한다.")
    @Test
    void deleteComment() {
        when(recruitCommentRepository.findById(any())).thenReturn(Optional.of(recruitComment));

        recruitCommentService.delete(user.getId(), recruitComment.getId());

        assertAll(
            () -> verify(recruitCommentRepository).findById(any()),
            () -> verify(recruitCommentRepository).delete(any())
        );
    }

    @DisplayName("댓글 작성자가 아닌 다른 사람이 삭제하면 예외가 발생한다.")
    @Test
    void deleteCommentByNonOwner() {
        Long nonOwnerId = 2L;
        Long commentId = recruitComment.getId();
        when(recruitCommentRepository.findById(any())).thenReturn(Optional.of(recruitComment));

        assertThatThrownBy(() -> recruitCommentService.delete(nonOwnerId, commentId))
            .isInstanceOf(AuthException.class);
    }
}
