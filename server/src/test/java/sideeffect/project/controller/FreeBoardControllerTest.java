package sideeffect.project.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import sideeffect.project.domain.freeboard.FreeBoard;
import sideeffect.project.domain.user.User;
import sideeffect.project.dto.freeboard.FreeBoardResponse;
import sideeffect.project.dto.freeboard.FreeBoardScrollResponse;
import sideeffect.project.service.FreeBoardService;

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
            .name("test")
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
            .andDo(print());
        verify(freeBoardService).findBoard(any());
    }

    @Test
    void scrollBoard() throws Exception {
        FreeBoard board1 = FreeBoard.builder().id(100L).userId(1L).content("게시판1입니다.").title("게시판1").build();
        FreeBoard board2 = FreeBoard.builder().id(99L).userId(1L).content("게시판2입니다.").title("게시판2").build();
        board1.associateUser(user);
        board2.associateUser(user);
        List<FreeBoardResponse> responses = FreeBoardResponse.listOf(List.of(board1, board2));
        FreeBoardScrollResponse scrollResponse = FreeBoardScrollResponse.of(responses, false);
        when(freeBoardService.findBoardScroll(any())).thenReturn(scrollResponse);

        mvc.perform(get("/api/free-board/scroll")
                .contentType(MediaType.APPLICATION_JSON)
                .param("lastId", "101")
                .param("size", "2"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()", 2).exists())
            .andDo(print());
    }
}
