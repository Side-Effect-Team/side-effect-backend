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
import org.springframework.mock.web.MockMultipartFile;
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
import sideeffect.project.domain.position.PositionType;
import sideeffect.project.domain.recruit.RecruitBoard;
import sideeffect.project.domain.stack.StackType;
import sideeffect.project.domain.user.User;
import sideeffect.project.domain.user.UserRoleType;
import sideeffect.project.dto.comment.RecruitCommentResponse;
import sideeffect.project.dto.like.LikeResult;
import sideeffect.project.dto.like.RecruitLikeResponse;
import sideeffect.project.dto.recruit.*;
import sideeffect.project.service.RecruitBoardService;
import sideeffect.project.service.RecruitLikeService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@AutoConfigureRestDocs
@WebMvcTest(RecruitBoardController.class)
@ExtendWith(RestDocumentationExtension.class)
class RecruitBoardControllerTest {

    @MockBean
    private RecruitBoardService recruitBoardService;

    @MockBean
    private RecruitLikeService recruitLikeService;

    private MockMvc mvc;
    private User user;
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
                .email("tester@naver.com")
                .password("1234")
                .nickname("테스터")
                .userRoleType(UserRoleType.ROLE_USER)
                .build();

        recruitBoard = RecruitBoard.builder()
                .id(1L)
                .title("모집 게시판")
                .contents("모집 게시판 내용")
                .build();

        objectMapper = new ObjectMapper();
    }

    @DisplayName("모집게시글을 조회한다.")
    @WithCustomUser
    @Test
    void findRecruitBoard() throws Exception {
        DetailedRecruitBoardResponse response = DetailedRecruitBoardResponse.builder()
            .id(1L)
            .writer(user.getNickname())
            .views(0)
            .userId(user.getId())
            .createdAt(null)
            .title("모집 게시글 제목")
            .projectName("프로젝트명1")
            .content("모집 게시글 내용")
            .positions(List.of(new DetailedBoardPositionResponse(1L, PositionType.BACKEND.getValue(), 3, 0, false)))
            .tags(List.of(new BoardStackResponse(StackType.SPRING.getValue(), "url")))
            .comments(RecruitCommentResponse.listOf(generateRecruitComments(1L,10L)))
            .build();

        given(recruitBoardService.findRecruitBoard(any(), any())).willReturn(response);

        mvc.perform(RestDocumentationRequestBuilders.get("/api/recruit-board/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.title").value(response.getTitle()))
            .andExpect(jsonPath("$.projectName").value(response.getProjectName()))
            .andExpect(jsonPath("$.content").value(response.getContent()))
            .andExpect(jsonPath("$.positions.length()").value(1))
            .andExpect(jsonPath("$.tags.length()").value(1))
            .andExpect(jsonPath("$.comments.length()").value(10))
            .andDo( // rest docs 문서 작성 시작
                MockMvcRestDocumentationWrapper.document("recruit-board/find",
                    resource(
                        ResourceSnippetParameters.builder()
                            .tag("모집게시판 API")
                            .description("모집게시판을 상세 조회한다.").
                            pathParameters( // path 파라미터 정보 입력
                                parameterWithName("id").description("모집 게시글 아이디")
                            ).responseFields( // response 필드 정보 입력
                                fieldWithPath("id").description("아이디"),
                                fieldWithPath("userId").description("작성자 아이디"),
                                fieldWithPath("writer").description("작성자"),
                                fieldWithPath("projectName").description("프로젝트명"),
                                fieldWithPath("views").description("조회수"),
                                fieldWithPath("title").description("제목"),
                                fieldWithPath("content").description("내용"),
                                fieldWithPath("imgSrc").description("게시글 이미지"),
                                fieldWithPath("like").description("좋아요 여부"),
                                fieldWithPath("likeNum").description("좋아요 수"),
                                fieldWithPath("createdAt").description("작성일"),
                                fieldWithPath("positions[].id").description("모집 포지션"),
                                fieldWithPath("positions[].positionType").description("모집 포지션"),
                                fieldWithPath("positions[].targetNumber").description("모집 포지션"),
                                fieldWithPath("positions[].currentNumber").description("모집 포지션"),
                                fieldWithPath("positions[].supported").description("모집 포지션"),
                                fieldWithPath("tags[].stackType").description("기술 태그"),
                                fieldWithPath("tags[].url").description("기술 태그"),
                                fieldWithPath("comments[].commentId").description("게시글 댓글"),
                                fieldWithPath("comments[].recruitBoardId").description("게시글 댓글"),
                                fieldWithPath("comments[].content").description("게시글 댓글"),
                                fieldWithPath("comments[].writer").description("게시글 댓글"),
                                fieldWithPath("comments[].writerId").description("게시글 댓글")
                            ).build())
                ));

        verify(recruitBoardService).findRecruitBoard(any(), any());
    }

    @DisplayName("모집게시글 목록을 조회한다.")
    @WithCustomUser
    @Test
    void findScrollRecruitBoard() throws Exception {
        RecruitBoardResponse response1 = RecruitBoardResponse.builder().
                id(10L).closed(false).title("모집 게시판1").views(10).like(false).likeNum(10).commentNum(1).createdAt(LocalDateTime.now()).positions(List.of("backend", "frontend")).tags(List.of("spring", "react")).build();
        RecruitBoardResponse response2 = RecruitBoardResponse.builder().
                id(5L).closed(true).title("모집 게시판2").views(5).like(true).likeNum(5).commentNum(0).createdAt(LocalDateTime.now()).positions(List.of("backend")).tags(List.of("spring")).build();
        RecruitBoardScrollResponse scrollResponse = RecruitBoardScrollResponse.of(List.of(response1, response2), false);

        given(recruitBoardService.findRecruitBoards(any(), any())).willReturn(scrollResponse);

        mvc.perform(RestDocumentationRequestBuilders.get("/api/recruit-board/scroll")
                .contentType(MediaType.APPLICATION_JSON)
                .param("lastId", "11")
                .param("size", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.recruitBoards.length()").value(2))
                .andExpect(jsonPath("$.lastId").value(5L))
                .andExpect(jsonPath("$.hasNext").value(false))
                .andDo(MockMvcRestDocumentationWrapper.document("recruit-board/scroll",
                        resource(
                                ResourceSnippetParameters.builder()
                                        .tag("모집게시판 API")
                                        .description("모집게시판을 스크롤 조회한다.")
                                        .requestParameters(
                                                parameterWithName("size").description("응답 받을 게시글 수"),
                                                parameterWithName("keyword").description("검색어(제목 + 내용)").optional(),
                                                parameterWithName("stackType").description("기술 스택 포함 검색").optional(),
                                                parameterWithName("lastId").description("이전 응답에서 가장 작은 ID 값, 없으면 첫 페이지").optional()
                                        )
                                        .responseFields(
                                            fieldWithPath("recruitBoards[].id").type(JsonFieldType.NUMBER).description("게시글 아이디"),
                                            fieldWithPath("recruitBoards[].closed").type(JsonFieldType.BOOLEAN).description("모집 마감 여부"),
                                            fieldWithPath("recruitBoards[].title").type(JsonFieldType.STRING).description("내용"),
                                            fieldWithPath("recruitBoards[].views").type(JsonFieldType.NUMBER).description("조회수"),
                                            fieldWithPath("recruitBoards[].like").type(JsonFieldType.BOOLEAN).description("좋아요 여부"),
                                            fieldWithPath("recruitBoards[].likeNum").type(JsonFieldType.NUMBER).description("좋아요 수"),
                                            fieldWithPath("recruitBoards[].commentNum").type(JsonFieldType.NUMBER).description("댓글 수"),
                                            fieldWithPath("recruitBoards[].createdAt").type(JsonFieldType.STRING).description("작성일"),
                                            fieldWithPath("recruitBoards[].positions[]").type(JsonFieldType.ARRAY).description("모집 포지션 종류"),
                                            fieldWithPath("recruitBoards[].tags[]").type(JsonFieldType.ARRAY).description("모집 기술 태그 종류"),
                                            fieldWithPath("lastId").type(JsonFieldType.NUMBER).description("응답한 게시글 중 마지막 아이디"),
                                            fieldWithPath("hasNext").type(JsonFieldType.BOOLEAN).description("다음 게시글 여부")
                                     ).build())
                ));

        verify(recruitBoardService).findRecruitBoards(any(), any());
    }

    @DisplayName("모집게시글 전체를 조회한다.")
    @WithCustomUser
    @Test
    void findAllRecruitBoard() throws Exception {
        RecruitBoardListResponse response1 = RecruitBoardListResponse.builder().
                id(10L).userId(user.getId()).title("모집 게시판1").projectName("프로젝트 명1").content("내용1").imgSrc("test.png").views(10).like(false).likeNum(10).createdAt(LocalDateTime.now())
                .positions(List.of(new BoardPositionResponse(1L, PositionType.BACKEND.getValue(), 3, 0), new BoardPositionResponse(2L, PositionType.FRONTEND.getValue(), 2, 0)))
                .tags(List.of(new BoardStackResponse(StackType.SPRING.getValue(), "tag.png"), new BoardStackResponse(StackType.REACT.getValue(), "tag.png"))).build();

        RecruitBoardListResponse response2 = RecruitBoardListResponse.builder().
                id(5L).userId(user.getId()).title("모집 게시판2").projectName("프로젝트 명2").content("내용2").imgSrc("test.png").views(5).like(true).likeNum(5).createdAt(LocalDateTime.now())
                .positions(List.of(new BoardPositionResponse(1L, PositionType.BACKEND.getValue(), 3, 0)))
                .tags(List.of(new BoardStackResponse(StackType.SPRING.getValue(), "tag.png"))).build();

        RecruitBoardAllResponse response = RecruitBoardAllResponse.of(List.of(response1, response2));

        given(recruitBoardService.findAllRecruitBoard(any())).willReturn(response);

        mvc.perform(RestDocumentationRequestBuilders.get("/api/recruit-board/all")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.recruitBoards.length()").value(2))
                .andDo(
                        MockMvcRestDocumentationWrapper.document("recruit-board/all",
                                resource(
                                        ResourceSnippetParameters.builder()
                                                .tag("모집게시판 API")
                                                .description("모집게시판을 전체 조회한다.")
                                                .responseFields(
                                                        fieldWithPath("recruitBoards[].id").type(JsonFieldType.NUMBER).description("게시글 아이디"),
                                                        fieldWithPath("recruitBoards[].userId").type(JsonFieldType.NUMBER).description("작성자 아이디"),
                                                        fieldWithPath("recruitBoards[].title").type(JsonFieldType.STRING).description("제목"),
                                                        fieldWithPath("recruitBoards[].projectName").type(JsonFieldType.STRING).description("프로젝트 이름"),
                                                        fieldWithPath("recruitBoards[].content").type(JsonFieldType.STRING).description("내용"),
                                                        fieldWithPath("recruitBoards[].imgSrc").type(JsonFieldType.STRING).description("게시글 이미지"),
                                                        fieldWithPath("recruitBoards[].views").type(JsonFieldType.NUMBER).description("조회수"),
                                                        fieldWithPath("recruitBoards[].like").type(JsonFieldType.BOOLEAN).description("좋아요 여부"),
                                                        fieldWithPath("recruitBoards[].likeNum").type(JsonFieldType.NUMBER).description("좋아요 수"),
                                                        fieldWithPath("recruitBoards[].createdAt").type(JsonFieldType.STRING).description("작성일"),
                                                        fieldWithPath("recruitBoards[].positions[].id").type(JsonFieldType.NUMBER).description("포지션 아이디"),
                                                        fieldWithPath("recruitBoards[].positions[].positionType").type(JsonFieldType.STRING).description("포지션 종류"),
                                                        fieldWithPath("recruitBoards[].positions[].targetNumber").type(JsonFieldType.NUMBER).description("포지션 인원 목표 수"),
                                                        fieldWithPath("recruitBoards[].positions[].currentNumber").type(JsonFieldType.NUMBER).description("포지션 인원 현재 수"),
                                                        fieldWithPath("recruitBoards[].tags[].stackType").type(JsonFieldType.STRING).description("기술 태그 종류"),
                                                        fieldWithPath("recruitBoards[].tags[].url").type(JsonFieldType.STRING).description("기술 태그 이미지")
                                                ).build())
                ));

        verify(recruitBoardService).findAllRecruitBoard(any());
    }

    @DisplayName("모집 게시판을 등록한다.")
    @WithCustomUser
    @Test
    void registerRecruitBoard() throws Exception {
        RecruitBoardRequest request = RecruitBoardRequest.builder()
                .title("모집 게시판1")
                .projectName("프로젝트 명")
                .content("글 내용이며, 최소 20글자를 입력해야 합니다. 글 내용이며, 최소 20글자를 입력해야 합니다.")
                .positions(List.of(new BoardPositionRequest(PositionType.BACKEND, 3), new BoardPositionRequest(PositionType.FRONTEND, 2)))
                .tags(List.of(StackType.SPRING, StackType.REACT))
                .build();

        RecruitBoardResponse response = RecruitBoardResponse.builder().
                id(1L).closed(false).title("모집 게시판1").views(0).like(false).likeNum(0).commentNum(0).createdAt(LocalDateTime.now()).positions(List.of("backend", "frontend")).tags(List.of("spring", "react")).build();

        given(recruitBoardService.register(any(), any())).willReturn(response);

        mvc.perform(RestDocumentationRequestBuilders.post("/api/recruit-board")
                        .with(csrf())
                        .header(HttpHeaders.AUTHORIZATION, "Bearer token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentationWrapper.document("recruit-board/register",
                                resource(
                                        ResourceSnippetParameters.builder()
                                                .tag("모집게시판 API")
                                                .description("모집게시판을 등록한다.")
                                                .requestHeaders(
                                                        headerWithName(HttpHeaders.AUTHORIZATION).description("Bearer + 토큰")
                                                )
                                                .requestFields(
                                                        fieldWithPath("title").type(JsonFieldType.STRING).description("제목"),
                                                        fieldWithPath("projectName").type(JsonFieldType.STRING).description("프로젝트 명"),
                                                        fieldWithPath("content").type(JsonFieldType.STRING).description("내용"),
                                                        fieldWithPath("positions[].positionType").type(JsonFieldType.STRING).description("모집 포지션"),
                                                        fieldWithPath("positions[].targetNumber").type(JsonFieldType.NUMBER).description("모집 인원"),
                                                        fieldWithPath("tags").type(JsonFieldType.ARRAY).description("기술 태그")
                                                )
                                                .responseFields(
                                                        fieldWithPath("id").type(JsonFieldType.NUMBER).description("게시글 아이디"),
                                                        fieldWithPath("closed").type(JsonFieldType.BOOLEAN).description("모집 마감 여부"),
                                                        fieldWithPath("title").type(JsonFieldType.STRING).description("내용"),
                                                        fieldWithPath("views").type(JsonFieldType.NUMBER).description("조회수"),
                                                        fieldWithPath("like").type(JsonFieldType.BOOLEAN).description("좋아요 여부"),
                                                        fieldWithPath("likeNum").type(JsonFieldType.NUMBER).description("좋아요 수"),
                                                        fieldWithPath("commentNum").type(JsonFieldType.NUMBER).description("댓글 수"),
                                                        fieldWithPath("createdAt").type(JsonFieldType.STRING).description("작성일"),
                                                        fieldWithPath("positions").type(JsonFieldType.ARRAY).description("모집 포지션 종류"),
                                                        fieldWithPath("tags").type(JsonFieldType.ARRAY).description("모집 기술 태그 종류")
                                                ).build())
                ));
    }

    @DisplayName("모집 게시판을 업데이트한다.")
    @WithCustomUser
    @Test
    void updateBoard() throws Exception {
        RecruitBoardUpdateRequest request = RecruitBoardUpdateRequest.builder()
                .title("모집 게시판 내용 수정")
                .projectName("프로젝트 명")
                .content("개발자 역량을 키우기 위해 진행하는 프로젝트입니다.")
                .tags(List.of(StackType.VUE, StackType.JAVA)).build();

        mvc.perform(RestDocumentationRequestBuilders.patch("/api/recruit-board/{id}", 1L)
                        .with(csrf())
                        .header(HttpHeaders.AUTHORIZATION, "Bearer token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentationWrapper.document("recruit-board/update",
                                resource(
                                        ResourceSnippetParameters.builder()
                                                .tag("모집게시판 API")
                                                .description("모집게시판을 수정한다.")
                                                .requestHeaders(
                                                        headerWithName(HttpHeaders.AUTHORIZATION).description("Bearer + 토큰")
                                                )
                                                .pathParameters(
                                                    parameterWithName("id").description("모집 게시글 아이디")
                                                )
                                                .requestFields(
                                                    fieldWithPath("title").type(JsonFieldType.STRING).description("제목"),
                                                    fieldWithPath("projectName").type(JsonFieldType.STRING).description("프로젝트 명"),
                                                    fieldWithPath("content").type(JsonFieldType.STRING).description("내용"),
                                                    fieldWithPath("tags").type(JsonFieldType.ARRAY).description("기술 태그")
                                                ).build())
                ));
    }

    @DisplayName("잘못된 유저가 모집 게시판을 업데이트 한다.")
    @WithCustomUser
    @Test
    void updateBoardByNotOwner() throws Exception {
        RecruitBoardUpdateRequest request = RecruitBoardUpdateRequest.builder().title("모집 게시판1")
                .projectName("프로젝트 명")
                .content("글 내용이며, 최소 20글자를 입력해야 합니다. 글 내용이며, 최소 20글자를 입력해야 합니다.").build();

        doThrow(new AuthException(ErrorCode.RECRUIT_BOARD_UNAUTHORIZED))
                .when(recruitBoardService).updateRecruitBoard(any(), any(), any());

        mvc.perform(patch("/api/recruit-board/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @DisplayName("모집 게시판에 포지션을 추가한다.")
    @WithCustomUser
    @Test
    void addRecruitBoardPosition() throws Exception {
        BoardPositionRequest request = BoardPositionRequest.builder().positionType(PositionType.DEVOPS).targetNumber(1).build();

        mvc.perform(RestDocumentationRequestBuilders.post("/api/recruit-board/{id}/add-position", 1L)
                        .with(csrf())
                        .header(HttpHeaders.AUTHORIZATION, "Bearer token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentationWrapper.document("recruit-board/add-position",
                                resource(
                                        ResourceSnippetParameters.builder()
                                                .tag("모집게시판 API")
                                                .description("모집게시판에 포지션을 추가한다.")
                                                .requestHeaders(
                                                        headerWithName(HttpHeaders.AUTHORIZATION).description("Bearer + 토큰")
                                                )
                                                .pathParameters(
                                                    parameterWithName("id").description("모집 게시글 아이디")
                                                )
                                                .requestFields(
                                                    fieldWithPath("positionType").type(JsonFieldType.STRING).description("포지션"),
                                                    fieldWithPath("targetNumber").type(JsonFieldType.NUMBER).description("포지션 인원 목표 수")
                                                ).build())
                ));
    }

    @DisplayName("잘못된 유저가 모집 게시판의 포지션을 추가한다.")
    @WithCustomUser
    @Test
    void addRecruitBoardPositionNonOwner() throws Exception {
        BoardPositionRequest request = BoardPositionRequest.builder().positionType(PositionType.BACKEND).targetNumber(3).build();

        doThrow(new AuthException(ErrorCode.RECRUIT_BOARD_UNAUTHORIZED))
                .when(recruitBoardService).addRecruitBoardPosition(any(), any(), any());

        mvc.perform(post("/api/recruit-board/1/add-position")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @DisplayName("모집 게시판을 삭제한다.")
    @WithCustomUser
    @Test
    void deleteBoard() throws Exception {
        mvc.perform(RestDocumentationRequestBuilders.delete("/api/recruit-board/{id}", 1L)
                        .with(csrf())
                        .header(HttpHeaders.AUTHORIZATION, "Bearer token"))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentationWrapper.document("recruit-board/delete",
                                resource(
                                        ResourceSnippetParameters.builder()
                                                .tag("모집게시판 API")
                                                .description("모집게시판을 삭제한다.")
                                                .requestHeaders(
                                                        headerWithName(HttpHeaders.AUTHORIZATION).description("Bearer + 토큰")
                                                )
                                                .pathParameters(
                                                    parameterWithName("id").description("모집 게시글 아이디")
                                                ).build())
                ));
    }

    @DisplayName("모집 게시판 글 작성자가 아니라면 삭제가 불가능하다.")
    @WithCustomUser
    @Test
    void deleteBoardByNotOwner() throws Exception {
        doThrow(new AuthException(ErrorCode.RECRUIT_BOARD_UNAUTHORIZED))
                .when(recruitBoardService).deleteRecruitBoard(any(), any());
        mvc.perform(delete("/api/recruit-board/1")
                        .with(csrf()))
                .andExpect(status().isForbidden());
    }

    @DisplayName("유저가 모집게시판에 좋아요를 누른다.")
    @WithCustomUser
    @Test
    void recruitBoardLikeOk() throws Exception {
        RecruitLikeResponse response = RecruitLikeResponse.builder().recruitBoardId(1L).userNickname("test1").message(LikeResult.LIKE.getMessage()).build();

        given(recruitLikeService.toggleLike(any(), any())).willReturn(response);

        mvc.perform(RestDocumentationRequestBuilders.post("/api/recruit-board/likes/{id}", 1L)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.recruitBoardId").value(1))
                .andExpect(jsonPath("$.userNickname").value("test1"))
                .andExpect(jsonPath("$.message").value(LikeResult.LIKE.getMessage()))
                .andDo(MockMvcRestDocumentationWrapper.document("recruit-board/likes",
                                resource(
                                        ResourceSnippetParameters.builder()
                                                .tag("모집게시판 API")
                                                .description("모집게시판에 좋아요를 누르거나 취소한다.")
                                                .requestHeaders(
                                                        headerWithName(HttpHeaders.AUTHORIZATION).description("Bearer + 토큰")
                                                )
                                                .pathParameters(
                                                    parameterWithName("id").description("모집 게시글 아이디")
                                                )
                                                .responseFields(
                                                    fieldWithPath("recruitBoardId").type(JsonFieldType.NUMBER).description("게시글 아이디"),
                                                    fieldWithPath("userNickname").type(JsonFieldType.STRING).description("사용자 닉네임"),
                                                    fieldWithPath("message").type(JsonFieldType.STRING).description("메시지(추천 또는 취소에 따라 다릅니다)")
                                                ).build())
                ));
    }

    @DisplayName("유저가 모집게시판에 좋아요를 취소한다.")
    @WithCustomUser
    @Test
    void recruitBoardLikeCancel() throws Exception {
        RecruitLikeResponse response = RecruitLikeResponse.builder().recruitBoardId(1L).userNickname("test1").message(LikeResult.CANCEL_LIKE.getMessage()).build();

        given(recruitLikeService.toggleLike(any(), any())).willReturn(response);

        mvc.perform(post("/api/recruit-board/likes/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.recruitBoardId").value(1))
                .andExpect(jsonPath("$.userNickname").value("test1"))
                .andExpect(jsonPath("$.message").value(LikeResult.CANCEL_LIKE.getMessage()))
                .andDo(print());
    }

    @DisplayName("이미지를 업로드한다.")
    @WithCustomUser
    @Test
    void uploadImage() throws Exception {
        MockMultipartFile multipartFile =
                new MockMultipartFile("file", "test.png", "image/png", "테스트 이미지".getBytes());

        mvc.perform(RestDocumentationRequestBuilders.multipart("/api/recruit-board/image/{id}", 1L)
                        .file(multipartFile)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer token")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentationWrapper.document("recruit-board/image-upload",
                                resource(
                                        ResourceSnippetParameters.builder()
                                                .tag("모집게시판 API")
                                                .description("모집게시판에 이미지를 업로드한다.")
                                                .requestHeaders(
                                                        headerWithName(HttpHeaders.AUTHORIZATION).description("Bearer + 토큰")
                                                )
                                                .pathParameters(
                                                    parameterWithName("id").description("모집 게시글 아이디")
                                                )
//                                                .requestParts(partWithName("file").description("업로드 이미지"))
                                .build())
                        ));
    }


    private List<RecruitComment> generateRecruitComments(Long startId, Long endId) {
        return LongStream.range(startId, endId + 1)
                .map(i -> startId + (endId - i))
                .mapToObj(this::generateRecruitComment).collect(Collectors.toList());
    }

    private RecruitComment generateRecruitComment(Long id) {
        RecruitComment comment = RecruitComment.builder().id(id).content("내용" + id).build();
        comment.associate(user, recruitBoard);
        return comment;
    }

}
