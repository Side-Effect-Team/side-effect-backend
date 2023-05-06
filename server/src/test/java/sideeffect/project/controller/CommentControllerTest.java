package sideeffect.project.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import sideeffect.project.common.exception.AuthException;
import sideeffect.project.common.exception.ErrorCode;
import sideeffect.project.config.WebSecurityConfig;
import sideeffect.project.domain.comment.Comment;
import sideeffect.project.domain.freeboard.FreeBoard;
import sideeffect.project.domain.user.User;
import sideeffect.project.domain.user.UserRoleType;
import sideeffect.project.dto.comment.CommentRequest;
import sideeffect.project.dto.comment.CommentResponse;
import sideeffect.project.security.UserDetailsImpl;
import sideeffect.project.security.UserDetailsServiceImpl;
import sideeffect.project.service.CommentService;

@Import(WebSecurityConfig.class)
@ComponentScan(basePackages = "sideeffect/project/security")
@WebMvcTest(CommentController.class)
class CommentControllerTest {

    @MockBean
    private UserDetailsServiceImpl userDetailsService;

    @MockBean
    private CommentService commentService;

    private User user;
    private MockMvc mvc;
    private Comment comment;
    private FreeBoard freeBoard;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp(WebApplicationContext context) {
        mvc = MockMvcBuilders.webAppContextSetup(context)
            .apply(springSecurity())
            .build();

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
    @Test
    void registerComment() throws Exception {
        generateToken();
        CommentRequest request = CommentRequest.builder()
            .freeBoardId(freeBoard.getId()).comment("좋은 프로젝트네요.").build();
        given(commentService.registerComment(any(), any())).willReturn(CommentResponse.of(comment));

        mvc.perform(post("/api/comments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andDo(print());
    }

    @DisplayName("요청을 보내 댓글을 수정한다.")
    @Test
    void updateComment() throws Exception {
        generateToken();
        CommentRequest request = CommentRequest.builder().comment("감사합니다.").build();

        mvc.perform(patch("/api/comments/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andDo(print());
    }

    @DisplayName("댓글의 주인이 아닌자가 수정 요청을 하면 예외가 발생")
    @Test
    void updateCommentByNonOwner() throws Exception {
        generateToken();
        CommentRequest request = CommentRequest.builder().comment("감사합니다.").build();
        doThrow(new AuthException(ErrorCode.COMMENT_UNAUTHORIZED)).when(commentService).update(any(), any(), any());

        mvc.perform(patch("/api/comments/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isForbidden())
            .andDo(print());
    }

    @DisplayName("요청을 보내 댓글을 삭제한다.")
    @Test
    void deleteComment() throws Exception {
        generateToken();

        mvc.perform(delete("/api/comments/1"))
            .andExpect(status().isOk())
            .andDo(print());
    }

    @DisplayName("댓글의 주인이 아닌자가 삭제 요청을 하면 예외가 발생")
    @Test
    void deleteCommentByNonOwner() throws Exception {
        generateToken();

        doThrow(new AuthException(ErrorCode.COMMENT_UNAUTHORIZED)).when(commentService).delete(any(), any());
        mvc.perform(delete("/api/comments/1"))
            .andExpect(status().isForbidden())
            .andDo(print());
    }

    private void generateToken() {
        UserDetailsImpl details = UserDetailsImpl.of(user);
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(details,
            details.getPassword(), details.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(token);
    }
}
