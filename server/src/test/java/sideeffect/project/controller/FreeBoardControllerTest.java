package sideeffect.project.controller;

import java.util.stream.Collectors;
import java.util.stream.LongStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import sideeffect.project.domain.comment.Comment;
import sideeffect.project.domain.freeboard.FreeBoard;
import sideeffect.project.domain.user.User;
import sideeffect.project.dto.freeboard.FreeBoardResponse;
import sideeffect.project.dto.freeboard.FreeBoardScrollResponse;
import sideeffect.project.service.FreeBoardService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FreeBoardController.class)
class FreeBoardControllerTest {

    @MockBean
    private FreeBoardService freeBoardService;

    private MockMvc mvc;

    private FreeBoardResponse response;
    private FreeBoard freeBoard;
    private User user;

    @BeforeEach
    void setUp(WebApplicationContext context) {
        mvc = MockMvcBuilders.webAppContextSetup(context)
            .build();

        user = User.builder()
            .id(1L)
            .password("1234")
            .build();

        freeBoard = FreeBoard.builder()
            .id(1L)
            .title("게시판입니다.")
            .projectUrl("url")
            .imgUrl("/test.jpg")
            .content("test")
            .build();
        freeBoard.associateUser(user);
        response = FreeBoardResponse.of(freeBoard);
    }

    @Test
    void findBoard() throws Exception {
        given(freeBoardService.findBoard(any())).willReturn(response);
        List<Comment> freeBoards = generateComments(1L, 10L);
        freeBoards.forEach(comment -> comment.associate(user, freeBoard));

        mvc.perform(get("/api/free-board/1")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.title", response.getTitle()).exists())
            .andExpect(jsonPath("$.views", response.getViews()).exists())
            .andExpect(jsonPath("$.userId", response.getUserId()).exists())
            .andExpect(jsonPath("$.title", response.getTitle()).exists())
            .andExpect(jsonPath("$.content", response.getContent()).exists())
            .andExpect(jsonPath("$.projectUrl", response.getProjectUrl()).exists())
            .andExpect(jsonPath("$.imgUrl", response.getImgUrl()).exists())
            .andExpect(jsonPath("$.comments.size()", 10).exists())
            .andDo(print());
        verify(freeBoardService).findBoard(any());
    }

    @Test
    void scrollBoard() throws Exception {
        List<FreeBoard> freeBoards = generateFreeBoards(91L, 100L);
        associateCommentsAndFreeBoards(freeBoards, generateComments(81L, 100L));
        List<FreeBoardResponse> responses = FreeBoardResponse.listOf(freeBoards);
        FreeBoardScrollResponse scrollResponse = FreeBoardScrollResponse.of(responses, true);
        when(freeBoardService.findScroll(any())).thenReturn(scrollResponse);

        mvc.perform(get("/api/free-board/scroll")
                .contentType(MediaType.APPLICATION_JSON)
                .param("lastId", "101")
                .param("size", "10"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()", 10).exists())
            .andDo(print());
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
        FreeBoard board = FreeBoard.builder().id(id).title(id + "번째 게시판").content(id + "번째 입니다.").build();
        board.associateUser(owner);
        return board;
    }
}
