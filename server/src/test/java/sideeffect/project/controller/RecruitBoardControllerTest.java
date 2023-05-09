package sideeffect.project.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import sideeffect.project.domain.position.PositionType;
import sideeffect.project.domain.recruit.ProgressType;
import sideeffect.project.domain.recruit.RecruitBoard;
import sideeffect.project.domain.recruit.RecruitBoardType;
import sideeffect.project.domain.stack.StackType;
import sideeffect.project.domain.user.User;
import sideeffect.project.dto.recruit.BoardPositionResponse;
import sideeffect.project.dto.recruit.BoardStackResponse;
import sideeffect.project.dto.recruit.RecruitBoardResponse;
import sideeffect.project.dto.recruit.RecruitBoardScrollResponse;
import sideeffect.project.service.RecruitBoardService;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RecruitBoardController.class)
class RecruitBoardControllerTest {

    @MockBean
    private RecruitBoardService recruitBoardService;

    private MockMvc mvc;
    private User user;

    @BeforeEach
    void setUp(WebApplicationContext context) {
        mvc = MockMvcBuilders.webAppContextSetup(context)
                .build();

        user = User.builder()
                .id(1L)
                .email("test@naver.com")
                .password("qwer1234!")
                .build();
    }

    @DisplayName("모집게시글을 조회한다.")
    @Test
    void findRecruitBoard() throws Exception {
        RecruitBoardResponse response = RecruitBoardResponse.builder()
                .id(1L)
                .views(0)
                .title("모집 게시글 제목")
                .projectName("프로젝트명1")
                .content("모집 게시글 내용")
                .recruitBoardType(RecruitBoardType.PROJECT.getValue())
                .progressType(ProgressType.ONLINE.getValue())
                .deadline(LocalDateTime.now())
                .expectedPeriod("3개월")
                .positions(List.of(new BoardPositionResponse(1L, PositionType.BACKEND.getValue(), 3, 0)))
                .tags(List.of(new BoardStackResponse(StackType.SPRING.getValue(), "url")))
                .build();

        given(recruitBoardService.findRecruitBoard(any())).willReturn(response);

        mvc.perform(get("/api/recruit-board/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value(response.getTitle()))
                .andExpect(jsonPath("$.projectName").value(response.getProjectName()))
                .andExpect(jsonPath("$.content").value(response.getContent()))
                .andExpect(jsonPath("$.recruitBoardType").value(response.getRecruitBoardType()))
                .andExpect(jsonPath("$.progressType").value(response.getProgressType()))
                .andExpect(jsonPath("$.deadline", response.getDeadline()).exists())
                .andExpect(jsonPath("$.expectedPeriod").value(response.getExpectedPeriod()))
                .andExpect(jsonPath("$.positions.length()").value(1))
                .andExpect(jsonPath("$.tags.length()").value(1))
                .andDo(print());

        verify(recruitBoardService).findRecruitBoard(any());
    }

    @DisplayName("모집게시글 목록을 조회한다.")
    @Test
    void findScrollRecruitBoard() throws Exception {
        RecruitBoard recruitBoard1 = RecruitBoard.builder().id(10L).title("모집 게시판1").recruitBoardType(RecruitBoardType.PROJECT).progressType(ProgressType.ONLINE).contents("모집합니다1.").build();
        RecruitBoard recruitBoard2 = RecruitBoard.builder().id(5L).title("모집 게시판2").recruitBoardType(RecruitBoardType.PROJECT).progressType(ProgressType.ONLINE).contents("모집합니다2.").build();
        recruitBoard1.associateUser(user);
        recruitBoard2.associateUser(user);
        List<RecruitBoardResponse> recruitBoardResponses = RecruitBoardResponse.listOf(List.of(recruitBoard1, recruitBoard2));
        RecruitBoardScrollResponse scrollResponse = RecruitBoardScrollResponse.of(recruitBoardResponses, false);

        given(recruitBoardService.findRecruitBoards(any())).willReturn(scrollResponse);

        mvc.perform(get("/api/recruit-board/scroll")
                .contentType(MediaType.APPLICATION_JSON)
                .param("lastId", "11")
                .param("size", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.recruitBoards.length()").value(2))
                .andExpect(jsonPath("$.lastId").value(5L))
                .andExpect(jsonPath("$.hasNext").value(false))
                .andDo(print());

        verify(recruitBoardService).findRecruitBoards(any());
    }

}