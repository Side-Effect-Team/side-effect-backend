package sideeffect.project.controller;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.springframework.restdocs.request.RequestDocumentation.partWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParts;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import sideeffect.project.common.docs.ControllerTestDocument;
import sideeffect.project.common.docs.freeBoard.FreeBoardDocsUtils;
import sideeffect.project.common.exception.AuthException;
import sideeffect.project.common.exception.ErrorCode;
import sideeffect.project.common.security.WithCustomUser;
import sideeffect.project.domain.comment.Comment;
import sideeffect.project.domain.freeboard.FreeBoard;
import sideeffect.project.domain.like.Like;
import sideeffect.project.domain.user.User;
import sideeffect.project.domain.user.UserRoleType;
import sideeffect.project.dto.freeboard.FreeBoardRequest;
import sideeffect.project.dto.freeboard.DetailedFreeBoardResponse;
import sideeffect.project.dto.freeboard.FreeBoardResponse;
import sideeffect.project.dto.freeboard.FreeBoardScrollResponse;
import sideeffect.project.dto.freeboard.RankResponse;
import sideeffect.project.service.FreeBoardService;

import java.util.List;

@WebMvcTest(FreeBoardController.class)
class FreeBoardControllerTest extends ControllerTestDocument {

    @MockBean
    private FreeBoardService freeBoardService;

    private FreeBoard freeBoard;
    private User user;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {

        user = User.builder()
            .id(1L)
            .email("tester@naver.com")
            .password("1234")
            .nickname("hello")
            .userRoleType(UserRoleType.ROLE_USER)
            .build();

        freeBoard = FreeBoard.builder()
            .id(1L)
            .title("강아지용 앱 프로젝트 입니다.")
            .projectUrl("url")
            .projectName("멍멍이 앱")
            .subTitle("우리집 강아지가 좋아해요!!!")
            .imgUrl("/test.jpg")
            .content("test")
            .build();
        freeBoard.associateUser(user);

        objectMapper = new ObjectMapper();
    }

    @DisplayName("특정 id의 게시판을 조회한다.")
    @WithCustomUser
    @Test
    void findBoard() throws Exception {
        int recommendNumber = 20;
        List<Comment> comments = generateComments(1L, 1L);
        comments.forEach(comment -> comment.associate(user, freeBoard));
        like(recommendNumber, freeBoard);
        DetailedFreeBoardResponse response = DetailedFreeBoardResponse.of(freeBoard, false);
        given(freeBoardService.findBoard(any(), any())).willReturn(response);

        mvc.perform(RestDocumentationRequestBuilders.get("/api/free-boards/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.title").value(response.getTitle()))
            .andExpect(jsonPath("$.views").value(response.getViews()))
            .andExpect(jsonPath("$.userId").value(response.getUserId()))
            .andExpect(jsonPath("$.title").value(response.getTitle()))
            .andExpect(jsonPath("$.writer").value(user.getNickname()))
            .andExpect(jsonPath("$.content").value(response.getContent()))
            .andExpect(jsonPath("$.projectUrl").value(response.getProjectUrl()))
            .andExpect(jsonPath("$.imgUrl").value(response.getImgUrl()))
            .andExpect(jsonPath("$.comments.size()").value(1))
            .andExpect(jsonPath("$.likeNum").value(recommendNumber))
            .andExpect(jsonPath("$.like").value(false))
            .andDo(print())
            .andDo(document("free-board/find", FreeBoardDocsUtils.getFreeBoardFindDocs()));
        verify(freeBoardService).findBoard(any(), any());
    }

    @DisplayName("게시판 스크롤 요청한다.")
    @WithCustomUser
    @Test
    void scrollBoard() throws Exception {
        List<FreeBoard> freeBoards = generateFreeBoards(91L, 100L);
        associateCommentsAndFreeBoards(freeBoards, generateComments(81L, 100L));
        List<FreeBoardResponse> responses = FreeBoardResponse.listOf(freeBoards);
        FreeBoardScrollResponse scrollResponse = FreeBoardScrollResponse.of(responses, true);
        given(freeBoardService.findScroll(any(), any())).willReturn(scrollResponse);

        mvc.perform(RestDocumentationRequestBuilders.get("/api/free-boards/scroll")
                .contentType(MediaType.APPLICATION_JSON)
                .param("lastId", "101")
                .param("size", "10"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.projects.length()").value(10))
            .andExpect(jsonPath("$.hasNext").value(true))
            .andExpect(jsonPath("$.lastId").value(91))
            .andDo(print())
            .andDo(document("free-board/scroll", FreeBoardDocsUtils.getFreeBoardScrollDocs()));
        verify(freeBoardService).findScroll(any(), any());
    }

    @DisplayName("검색 스크롤을 요청한다.")
    @WithCustomUser
    @Test
    void scrollBoardWithKeyWord() throws Exception {
        List<FreeBoard> freeBoards = generateFreeBoards(91L, 100L);
        associateCommentsAndFreeBoards(freeBoards, generateComments(81L, 100L));
        List<FreeBoardResponse> responses = FreeBoardResponse.listOf(freeBoards);
        FreeBoardScrollResponse scrollResponse = FreeBoardScrollResponse.of(responses, true);
        given(freeBoardService.findScrollWithKeyword(any(), any())).willReturn(scrollResponse);

        mvc.perform(get("/api/free-boards/scroll")
                .contentType(MediaType.APPLICATION_JSON)
                .param("lastId", "101")
                .param("size", "10")
                .param("keyword", "번째"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.projects.length()").value(10))
            .andExpect(jsonPath("$.hasNext").value(true))
            .andExpect(jsonPath("$.lastId").value(91))
            .andDo(print());
        verify(freeBoardService).findScrollWithKeyword(any(), any());
    }

    @DisplayName("랭킹 게시판을 가져온다.")
    @WithCustomUser
    @Test
    void getRankBoard() throws Exception {
        List<FreeBoard> freeBoards = generateLikeBoards();
        given(freeBoardService.findRankFreeBoards(any())).willReturn(RankResponse.listOf(freeBoards));

        mvc.perform(get("/api/free-boards/rank")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.size()").value(6))
            .andDo(print());
    }

    @DisplayName("게시판을 등록한다.")
    @WithCustomUser
    @Test
    void registerBoard() throws Exception {
        FreeBoardRequest request = FreeBoardRequest.builder()
            .projectUrl("http://1234test.co.kr")
            .content("test")
            .title("게시판 입니다")
            .projectName("게시판")
            .subTitle("테스트 게시판")
            .build();
        given(freeBoardService.register(any(), any())).willReturn(freeBoard);
        mvc.perform(RestDocumentationRequestBuilders.post("/api/free-boards")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .with(csrf()))
            .andExpect(status().isOk())
            .andDo(print())
            .andDo(document("free-board/register",
                resource(FreeBoardDocsUtils
                    .getFreeBoardRequestDocs()
                    .description("게시판을 등록")
                    .build())));
    }

    @DisplayName("게시판을 업데이트한다.")
    @WithCustomUser
    @Test
    void updateBoard() throws Exception {
        FreeBoardRequest request = FreeBoardRequest.builder().content("update").build();
        mvc.perform(RestDocumentationRequestBuilders.patch("/api/free-boards/{id}", 1L)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .with(csrf()))
            .andExpect(status().isOk())
            .andDo(document("free-board/update",
                resource(FreeBoardDocsUtils
                    .getFreeBoardRequestDocs()
                    .description("게시판을 업데이트")
                    .build())));
    }

    @DisplayName("잘못된 유저가 게시판을 업데이트 한다.")
    @WithCustomUser
    @Test
    void updateBoardByNotOwner() throws Exception {
        FreeBoardRequest request = FreeBoardRequest.builder().content("update").build();
        doThrow(new AuthException(ErrorCode.FREE_BOARD_UNAUTHORIZED))
            .when(freeBoardService).updateBoard(any(), any(), any());
        mvc.perform(patch("/api/free-boards/1")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isForbidden());
    }

    @DisplayName("게시판을 삭제한다.")
    @WithCustomUser
    @Test
    void deleteBoard() throws Exception {
        mvc.perform(RestDocumentationRequestBuilders.delete("/api/free-boards/{id}", 1L)
                .with(csrf()))
            .andExpect(status().isOk())
            .andDo(document("free-board/delete",
                resource(FreeBoardDocsUtils
                    .getFreeBoardPathParametersDocs()
                    .description("게시판 삭제")
                    .build())));
    }

    @DisplayName("게시판 주인이 아닌 사람이 삭제")
    @WithCustomUser
    @Test
    void deleteBoardByNotOwner() throws Exception {
        doThrow(new AuthException(ErrorCode.FREE_BOARD_UNAUTHORIZED))
            .when(freeBoardService).deleteBoard(any(), any());
        mvc.perform(delete("/api/free-boards/1")
                .with(csrf()))
            .andExpect(status().isForbidden());
    }

    @DisplayName("이미지를 업로드한다.")
    @WithCustomUser
    @Test
    void uploadImage() throws Exception {
        MockMultipartFile multipartFile =
            new MockMultipartFile("file", "test.png", "image/png", "이미지".getBytes());
        mvc.perform(RestDocumentationRequestBuilders.multipart("/api/free-boards/image/{id}", 1L)
                .file(multipartFile)
                .with(csrf()))
            .andExpect(status().isOk())
            .andDo(document("upload",
                resource(FreeBoardDocsUtils.getFreeBoardPathParametersDocs().description("이미지 업로드").build()),
                requestParts(partWithName("file").description("업로드할 이미지 파일").optional())));
    }

    private List<FreeBoard> generateLikeBoards() {
        List<FreeBoard> freeBoards = new ArrayList<>();
        for (int i = 6; i >= 1; i--) {
            FreeBoard board = FreeBoard.builder().title("게시판" + i).content("내용" + i).build();
            board.associateUser(user);
            like(i, board);
            freeBoards.add(board);
        }
        return freeBoards;
    }

    private void like(int number, FreeBoard freeBoard) {
        IntStream.range(0, number)
            .forEach((id) -> Like.like(User.builder().id((long) id).build(), freeBoard));
    }

    private void associateCommentsAndFreeBoards(List<FreeBoard> freeBoards, List<Comment> comments) {
        int commentPerFreeBoard = comments.size() / freeBoards.size();
        for (int i = 0; i < freeBoards.size(); i++) {
            FreeBoard board = freeBoards.get(i);
            User owner = User.builder().id((long) i).nickname("유저" + i).build();
            comments.subList(commentPerFreeBoard * i, commentPerFreeBoard * (i + 1))
                .forEach(comment -> comment.associate(owner, board));
        }
    }

    private List<Comment> generateComments(Long startId, Long endId) {
        return LongStream.range(startId, endId + 1)
            .map(i -> startId + (endId - i))
            .mapToObj(this::generateComment).collect(Collectors.toList());
    }

    private List<FreeBoard> generateFreeBoards(Long startId, Long endId) {
        return LongStream.range(startId, endId + 1)
            .map(i -> startId + (endId - i))
            .mapToObj(this::generateBoard).collect(Collectors.toList());
    }

    private Comment generateComment(Long id) {
        Comment comment = new Comment("댓글" + id);
        comment.setId(id);
        return comment;
    }

    private FreeBoard generateBoard(Long id) {
        User owner = User.builder().id(id).nickname("유저" + id).build();
        FreeBoard board = FreeBoard.builder()
            .id(id)
            .title(id + "번째 게시판")
            .imgUrl("DefaultProjectImg.jpg")
            .content(id + "번째 입니다.")
            .subTitle("게시판" + id)
            .build();
        board.associateUser(owner);
        return board;
    }
}
