package sideeffect.project.controller;

import com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper;
import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
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

import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@AutoConfigureRestDocs
@WebMvcTest(RecruitCommentController.class)
@ExtendWith(RestDocumentationExtension.class)
class RecruitCommentControllerTest {

    @MockBean
    private RecruitCommentService recruitCommentService;

    private User user;
    private MockMvc mvc;
    private RecruitComment recruitComment;
    private RecruitBoard recruitBoard;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp(WebApplicationContext context, RestDocumentationContextProvider restDocumentationContextProvider) {
        mvc = MockMvcBuilders.webAppContextSetup(context)
            .apply(springSecurity())
            .apply(documentationConfiguration(restDocumentationContextProvider)
                    .operationPreprocessors()
                    .withRequestDefaults(prettyPrint())
                    .withResponseDefaults(prettyPrint()))
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
            .boardId(recruitBoard.getId()).content(recruitComment.getContent()).build();


        given(recruitCommentService.registerComment(any(), any())).willReturn(RecruitCommentResponse.of(recruitComment));

        mvc.perform(RestDocumentationRequestBuilders.post("/api/recruit-comments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .with(csrf())
                .header(HttpHeaders.AUTHORIZATION, "Bearer token"))
            .andExpect(status().isOk())
            .andDo(MockMvcRestDocumentationWrapper.document("recruit-comments/register",
                    resource(
                            ResourceSnippetParameters.builder()
                                    .tag("모집게시판 댓글 API")
                                    .description("모집게시판 댓글을 등록한다.")
                                    .requestHeaders(
                                                    headerWithName(HttpHeaders.AUTHORIZATION).description("Bearer + 토큰")
                                            )
                                    .requestFields(
                                        fieldWithPath("boardId").type(JsonFieldType.NUMBER).description("게시글 아이디"),
                                        fieldWithPath("content").type(JsonFieldType.STRING).description("내용")
                                    )
                                    .responseFields(
                                        fieldWithPath("commentId").type(JsonFieldType.NUMBER).description("댓글 아이디"),
                                        fieldWithPath("recruitBoardId").type(JsonFieldType.NUMBER).description("게시글 아이디"),
                                        fieldWithPath("content").type(JsonFieldType.STRING).description("내용"),
                                        fieldWithPath("writer").type(JsonFieldType.STRING).description("작성자"),
                                        fieldWithPath("writerId").type(JsonFieldType.NUMBER).description("작성자 아이디")
                                    ).build())
            ));
    }

    @DisplayName("모집게시판 댓글을 수정한다.")
    @WithCustomUser
    @Test
    void updateComment() throws Exception {
        String content = "수정 내용";
        CommentUpdateRequest request = new CommentUpdateRequest(content);

        mvc.perform(RestDocumentationRequestBuilders.patch("/api/recruit-comments/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .header(HttpHeaders.AUTHORIZATION, "Bearer token")
                .with(csrf()))
            .andExpect(status().isOk())
            .andDo(MockMvcRestDocumentationWrapper.document("recruit-comments/update",
                            resource(
                                    ResourceSnippetParameters.builder()
                                            .tag("모집게시판 댓글 API")
                                            .description("모집게시판 댓글을 수정한다.")
                                            .requestHeaders(
                                                    headerWithName(HttpHeaders.AUTHORIZATION).description("Bearer + 토큰")
                                            )
                                            .pathParameters(
                                                parameterWithName("id").description("댓글 아이디")
                                            )
                                            .requestFields(
                                                fieldWithPath("content").type(JsonFieldType.STRING).description("내용")
                                            ).build())
            ));
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

        mvc.perform(RestDocumentationRequestBuilders.delete("/api/recruit-comments/{id}", 1L)
                .with(csrf())
                .header(HttpHeaders.AUTHORIZATION, "Bearer token"))
            .andExpect(status().isOk())
            .andDo(MockMvcRestDocumentationWrapper.document("recruit-comments/delete",
                            resource(
                                    ResourceSnippetParameters.builder()
                                            .tag("모집게시판 댓글 API")
                                            .description("모집게시판 댓글을 삭제한다.")
                                            .requestHeaders(
                                                    headerWithName(HttpHeaders.AUTHORIZATION).description("Bearer + 토큰")
                                            )
                                            .pathParameters(
                                                parameterWithName("id").description("댓글 아이디")
                                            ).build())
            ));
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
