package sideeffect.project.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import sideeffect.project.common.exception.AuthException;
import sideeffect.project.common.exception.ErrorCode;
import sideeffect.project.common.security.WithCustomUser;
import sideeffect.project.domain.comment.RecruitComment;
import sideeffect.project.domain.recruit.RecruitBoard;
import sideeffect.project.domain.user.User;
import sideeffect.project.domain.user.UserRoleType;
import sideeffect.project.dto.comment.CommentRequest;
import sideeffect.project.dto.comment.CommentUpdateRequest;
import sideeffect.project.dto.comment.RecruitCommentRequest;
import sideeffect.project.dto.comment.RecruitCommentResponse;
import sideeffect.project.service.RecruitCommentService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RecruitCommentController.class)
class RecruitCommentControllerTest {

    @MockBean
    private RecruitCommentService recruitCommentService;

    private User user;
    private MockMvc mvc;
    private RecruitComment recruitComment;
    private RecruitBoard recruitBoard;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp(WebApplicationContext context) {
        mvc = MockMvcBuilders.webAppContextSetup(context)
            .apply(springSecurity())
            .build();

        user = User.builder()
            .id(1L)
            .email("test@naver.com")
            .nickname("test")
            .password("1234")
            .userRoleType(UserRoleType.ROLE_USER)
            .build();

        recruitBoard = RecruitBoard.builder()
                .id(1L)
                .title("모집 게시판")
                .contents("모집 게시판 내용")
                .build();

        recruitBoard.associateUser(user);

        recruitComment = RecruitComment.builder().id(1L).content("댓글 내용").build();
        recruitComment.associate(user, recruitBoard);

        objectMapper = new ObjectMapper();
    }


    @DisplayName("모집게시판 댓글을 등록한다.")
    @WithCustomUser
    @Test
    void registerComment() throws Exception {
        RecruitCommentRequest request = RecruitCommentRequest.builder()
            .boardId(recruitBoard.getId()).content("참여 가능하나요?").build();

        given(recruitCommentService.registerComment(any(), any())).willReturn(RecruitCommentResponse.of(recruitComment));

        mvc.perform(post("/api/recruit-comments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .with(csrf()))
            .andExpect(status().isOk())
            .andDo(print());
    }

    @DisplayName("모집게시판 댓글을 수정한다.")
    @WithCustomUser
    @Test
    void updateComment() throws Exception {
        String content = "수정 내용";
        CommentUpdateRequest request = new CommentUpdateRequest(content);

        mvc.perform(patch("/api/recruit-comments/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .with(csrf()))
            .andExpect(status().isOk())
            .andDo(print());
    }

    @DisplayName("댓글의 주인이 아닌자가 수정 요청을 하면 예외가 발생")
    @WithCustomUser
    @Test
    void updateCommentByNonOwner() throws Exception {
        CommentRequest request = CommentRequest.builder().content("수정 내용").build();

        doThrow(new AuthException(ErrorCode.COMMENT_UNAUTHORIZED)).when(recruitCommentService).update(any(), any(), any());

        mvc.perform(patch("/api/recruit-comments/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isForbidden())
            .andDo(print());
    }

    @DisplayName("모집게시판 댓글을 삭제한다.")
    @WithCustomUser
    @Test
    void deleteComment() throws Exception {

        mvc.perform(delete("/api/recruit-comments/1")
                .with(csrf()))
            .andExpect(status().isOk())
            .andDo(print());
    }

    @DisplayName("댓글의 주인이 아닌자가 삭제 요청을 하면 예외가 발생")
    @WithCustomUser
    @Test
    void deleteCommentByNonOwner() throws Exception {

        doThrow(new AuthException(ErrorCode.COMMENT_UNAUTHORIZED)).when(recruitCommentService).delete(any(), any());

        mvc.perform(delete("/api/recruit-comments/1"))
            .andExpect(status().isForbidden())
            .andDo(print());
    }

}
