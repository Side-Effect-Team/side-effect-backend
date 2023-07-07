package sideeffect.project.controller;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import sideeffect.project.common.docs.ControllerTestDocument;
import sideeffect.project.common.docs.freeBoard.FreeBoardCommentDocsUtils;
import sideeffect.project.common.exception.AuthException;
import sideeffect.project.common.exception.ErrorCode;
import sideeffect.project.common.security.WithCustomUser;
import sideeffect.project.domain.comment.Comment;
import sideeffect.project.domain.freeboard.FreeBoard;
import sideeffect.project.domain.user.User;
import sideeffect.project.domain.user.UserRoleType;
import sideeffect.project.dto.comment.CommentRequest;
import sideeffect.project.dto.comment.CommentResponse;
import sideeffect.project.dto.comment.CommentUpdateRequest;
import sideeffect.project.service.CommentService;

@WebMvcTest(CommentController.class)
class CommentControllerTest extends ControllerTestDocument {

    @MockBean
    private CommentService commentService;

    private User user;
    private Comment comment;
    private FreeBoard freeBoard;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        user = User.builder()
            .id(1L)
            .email("test@naver.com")
            .nickname("tester")
            .password("1234")
            .userRoleType(UserRoleType.ROLE_USER)
            .build();

        freeBoard = FreeBoard.builder()
            .id(1L)
            .title("test")
            .content("hello")
            .build();
        freeBoard.associateUser(user);

        comment = new Comment("좋은 프로젝트네요");
        comment.associate(user, freeBoard);

        objectMapper = new ObjectMapper();
    }


    @DisplayName("요청을 보내 댓글을 등록한다.")
    @WithCustomUser
    @Test
    void registerComment() throws Exception {
        CommentRequest request = CommentRequest.builder()
            .boardId(freeBoard.getId()).content("좋은 프로젝트네요.").build();
        given(commentService.registerComment(any(), any())).willReturn(CommentResponse.of(comment));

        mvc.perform(RestDocumentationRequestBuilders.post("/api/comments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .with(csrf()))
            .andExpect(status().isOk())
            .andDo(print())
            .andDo(document("free-board/comment/register", FreeBoardCommentDocsUtils
                .getFreeBoardRegisterCommentDocs()));
    }

    @DisplayName("요청을 보내 댓글을 수정한다.")
    @WithCustomUser
    @Test
    void updateComment() throws Exception {
        String content = "감사합니다.";
        CommentUpdateRequest request = new CommentUpdateRequest(content);

        mvc.perform(RestDocumentationRequestBuilders.patch("/api/comments/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .with(csrf()))
            .andExpect(status().isOk())
            .andDo(print())
            .andDo(document("free-board/comment/update", FreeBoardCommentDocsUtils
                .getFreeBoardUpdateCommentDocs()));
    }

    @DisplayName("댓글의 주인이 아닌자가 수정 요청을 하면 예외가 발생")
    @WithCustomUser
    @Test
    void updateCommentByNonOwner() throws Exception {
        CommentRequest request = CommentRequest.builder().content("감사합니다.").build();
        doThrow(new AuthException(ErrorCode.COMMENT_UNAUTHORIZED)).when(commentService).update(any(), any(), any());

        mvc.perform(patch("/api/comments/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isForbidden())
            .andDo(print());
    }

    @DisplayName("요청을 보내 댓글을 삭제한다.")
    @WithCustomUser
    @Test
    void deleteComment() throws Exception {

        mvc.perform(RestDocumentationRequestBuilders.delete("/api/comments/{id}", 1L)
                .with(csrf()))
            .andExpect(status().isOk())
            .andDo(print())
            .andDo(document("free-board/comment/delete", FreeBoardCommentDocsUtils
            .getFreeBoardDeleteCommentDocs()));
    }

    @DisplayName("댓글의 주인이 아닌자가 삭제 요청을 하면 예외가 발생")
    @WithCustomUser
    @Test
    void deleteCommentByNonOwner() throws Exception {

        doThrow(new AuthException(ErrorCode.COMMENT_UNAUTHORIZED)).when(commentService).delete(any(), any());
        mvc.perform(delete("/api/comments/1"))
            .andExpect(status().isForbidden())
            .andDo(print());
    }

}
